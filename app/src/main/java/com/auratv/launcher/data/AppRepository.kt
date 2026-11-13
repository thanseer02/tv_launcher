package com.auratv.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import com.auratv.launcher.model.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AppRepository(
    private val context: Context,
    private val appDao: AppDao,
    private val iconDiskCache: IconDiskCache
) {
    private val packageManager: PackageManager = context.packageManager

    val visibleApps: Flow<List<AppItem>> = appDao.getAllVisibleApps()
    val allApps: Flow<List<AppItem>> = appDao.getAllApps()
    val favoriteApps: Flow<List<AppItem>> = appDao.getFavoriteApps()
    val recentApps: Flow<List<AppItem>> = appDao.getRecentApps()

    suspend fun refreshApps(iconPackPackage: String? = null, forceReloadIcons: Boolean = false) = withContext(Dispatchers.IO) {
        val leanbackIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }
        val normalIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val leanbackActivities: List<ResolveInfo> = packageManager.queryIntentActivities(leanbackIntent, 0)
        val normalActivities: List<ResolveInfo> = packageManager.queryIntentActivities(normalIntent, 0)

        val selfPackage = context.packageName
        val map = linkedMapOf<String, ResolveInfo>()

        for (ri in leanbackActivities) {
            val pkg = ri.activityInfo.packageName
            if (pkg != selfPackage) {
                map[pkg] = ri
            }
        }
        for (ri in normalActivities) {
            val pkg = ri.activityInfo.packageName
            if (pkg != selfPackage && !map.containsKey(pkg)) {
                map[pkg] = ri
            }
        }

        val existingApps = appDao.getAllAppsSnapshot().associateBy { it.packageName }
        val updatedList = mutableListOf<AppItem>()

        for ((pkg, resolveInfo) in map) {
            val existing = existingApps[pkg]
            val isLeanback = leanbackActivities.any { it.activityInfo.packageName == pkg }
            val label = resolveInfo.loadLabel(packageManager).toString()
            val activityName = resolveInfo.activityInfo.name

            val bannerDrawable = resolveInfo.activityInfo.loadBanner(packageManager)
            val iconDrawable = loadIconWithIconPack(pkg, resolveInfo, iconPackPackage)

            var bannerPath = existing?.bannerPath
            var iconPath = existing?.iconPath
            var dominantColor = existing?.dominantColor ?: 0xFF1E293B.toInt()

            if (forceReloadIcons || iconPath == null) {
                try {
                    iconPath = iconDiskCache.saveDrawable(iconDrawable, pkg, isBanner = false)
                    dominantColor = iconDiskCache.extractDominantColor(iconDrawable)
                } catch (e: Exception) {
                    // Fallback to default color
                }
            }

            if ((forceReloadIcons || bannerPath == null) && bannerDrawable != null) {
                try {
                    bannerPath = iconDiskCache.saveDrawable(bannerDrawable, pkg, isBanner = true)
                } catch (e: Exception) {
                    bannerPath = null
                }
            }

            val appInfo = try { packageManager.getApplicationInfo(pkg, 0) } catch (e: Exception) { null }
            val installTime = try { packageManager.getPackageInfo(pkg, 0).firstInstallTime } catch (e: Exception) { 0L }
            val category = detectCategory(appInfo)

            val appItem = AppItem(
                packageName = pkg,
                activityName = activityName,
                label = label,
                customLabel = existing?.customLabel,
                isLeanback = isLeanback,
                bannerPath = bannerPath,
                iconPath = iconPath,
                customIconPath = existing?.customIconPath,
                dominantColor = dominantColor,
                isFavorite = existing?.isFavorite ?: false,
                favoriteOrder = existing?.favoriteOrder ?: Int.MAX_VALUE,
                isHidden = existing?.isHidden ?: false,
                lastLaunchTime = existing?.lastLaunchTime ?: 0L,
                launchCount = existing?.launchCount ?: 0,
                installTime = installTime,
                category = category
            )
            updatedList.add(appItem)
        }

        appDao.insertOrUpdateAll(updatedList)

        for (existing in existingApps.values) {
            if (!map.containsKey(existing.packageName)) {
                appDao.deleteByPackage(existing.packageName)
                iconDiskCache.removeCachedFiles(existing.packageName)
            }
        }
    }

    private fun loadIconWithIconPack(pkg: String, resolveInfo: ResolveInfo, iconPackPackage: String?): Drawable {
        if (!iconPackPackage.isNullOrEmpty()) {
            try {
                val packResources = packageManager.getResourcesForApplication(iconPackPackage)
                val sanitizedName = pkg.replace(".", "_")
                val resId = packResources.getIdentifier(sanitizedName, "drawable", iconPackPackage)
                if (resId != 0) {
                    return packResources.getDrawable(resId, null)
                }
            } catch (e: Exception) {
                // Fall back to app's own icon
            }
        }
        return resolveInfo.activityInfo.loadIcon(packageManager)
    }

    private fun detectCategory(info: ApplicationInfo?): String {
        if (info == null) return "Other"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return when (info.category) {
                ApplicationInfo.CATEGORY_GAME -> "Games"
                ApplicationInfo.CATEGORY_VIDEO, ApplicationInfo.CATEGORY_AUDIO -> "Streaming & Media"
                ApplicationInfo.CATEGORY_IMAGE, ApplicationInfo.CATEGORY_SOCIAL -> "Social & Photos"
                ApplicationInfo.CATEGORY_NEWS -> "News & Weather"
                ApplicationInfo.CATEGORY_PRODUCTIVITY -> "Tools & Productivity"
                else -> "Other"
            }
        }
        return "Other"
    }

    suspend fun onPackageRemoved(packageName: String) = withContext(Dispatchers.IO) {
        appDao.deleteByPackage(packageName)
        iconDiskCache.removeCachedFiles(packageName)
    }

    suspend fun onPackageAddedOrUpdated(packageName: String) = withContext(Dispatchers.IO) {
        try {
            val leanbackIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
                `package` = packageName
            }
            var activities = packageManager.queryIntentActivities(leanbackIntent, 0)
            var isLeanback = true

            if (activities.isEmpty()) {
                val normalIntent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    `package` = packageName
                }
                activities = packageManager.queryIntentActivities(normalIntent, 0)
                isLeanback = false
            }

            val ri = activities.firstOrNull() ?: return@withContext
            val existing = appDao.getApp(packageName)

            val iconDrawable = ri.activityInfo.loadIcon(packageManager)
            val bannerDrawable = ri.activityInfo.loadBanner(packageManager)
            val iconPath = iconDiskCache.saveDrawable(iconDrawable, packageName, isBanner = false)
            val bannerPath = bannerDrawable?.let { iconDiskCache.saveDrawable(it, packageName, isBanner = true) }
            val dominantColor = iconDiskCache.extractDominantColor(iconDrawable)
            val installTime = try { packageManager.getPackageInfo(packageName, 0).firstInstallTime } catch (e: Exception) { 0L }

            val updated = AppItem(
                packageName = packageName,
                activityName = ri.activityInfo.name,
                label = ri.loadLabel(packageManager).toString(),
                customLabel = existing?.customLabel,
                isLeanback = isLeanback,
                bannerPath = bannerPath,
                iconPath = iconPath,
                customIconPath = existing?.customIconPath,
                dominantColor = dominantColor,
                isFavorite = existing?.isFavorite ?: false,
                favoriteOrder = existing?.favoriteOrder ?: Int.MAX_VALUE,
                isHidden = existing?.isHidden ?: false,
                lastLaunchTime = existing?.lastLaunchTime ?: 0L,
                launchCount = existing?.launchCount ?: 0,
                installTime = installTime,
                category = detectCategory(ri.activityInfo.applicationInfo)
            )
            appDao.insertOrUpdate(updated)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun toggleFavorite(packageName: String) = withContext(Dispatchers.IO) {
        val app = appDao.getApp(packageName) ?: return@withContext
        val newFavoriteState = !app.isFavorite
        val order = if (newFavoriteState) {
            val favorites = appDao.getFavoriteApps().first()
            favorites.size
        } else {
            Int.MAX_VALUE
        }
        appDao.setFavorite(packageName, newFavoriteState, order)
    }

    suspend fun reorderFavorites(favorites: List<AppItem>) = withContext(Dispatchers.IO) {
        favorites.forEachIndexed { index, app ->
            appDao.setFavorite(app.packageName, true, index)
        }
    }

    suspend fun toggleHidden(packageName: String) = withContext(Dispatchers.IO) {
        val app = appDao.getApp(packageName) ?: return@withContext
        appDao.setHidden(packageName, !app.isHidden)
    }

    suspend fun updateCustomLabel(packageName: String, label: String?) = withContext(Dispatchers.IO) {
        appDao.setCustomLabel(packageName, label?.takeIf { it.isNotBlank() })
    }

    suspend fun updateCustomIcon(packageName: String, iconPath: String?) = withContext(Dispatchers.IO) {
        appDao.setCustomIcon(packageName, iconPath)
    }

    suspend fun recordAppLaunch(packageName: String) = withContext(Dispatchers.IO) {
        appDao.recordLaunch(packageName, System.currentTimeMillis())
    }

    suspend fun resetCustomizations() = withContext(Dispatchers.IO) {
        appDao.resetCustomizations()
    }
}
