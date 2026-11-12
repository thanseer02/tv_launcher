package com.leantv.launcher.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Build
import com.leantv.launcher.data.model.AppModel
import com.leantv.launcher.data.source.PreferencesDataSource
import com.leantv.launcher.util.AppConstants
import com.leantv.launcher.util.MemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AppRepository handles real application discovery using Android PackageManager,
 * queries both Leanback and standard Launchers, merges them without duplicates,
 * and handles app icon/banner retrieval safely off the main thread.
 */
class AppRepository(
    private val context: Context,
    private val preferencesDataSource: PreferencesDataSource
) {

    private val packageManager: PackageManager = context.packageManager
    private val selfPackageName: String = context.packageName

    /**
     * Query all launchable applications on the TV.
     * Includes both Leanback-optimized TV applications and standard sideloaded apps.
     */
    suspend fun getInstalledApps(includeHidden: Boolean = false): List<AppModel> = withContext(Dispatchers.IO) {
        val leanbackIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }
        val standardIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager.MATCH_ALL
        } else {
            0
        }

        val leanbackApps = queryActivities(leanbackIntent, flags)
        val standardApps = queryActivities(standardIntent, flags)

        val favorites = preferencesDataSource.getFavoritePackageNames()
        val hiddenApps = preferencesDataSource.getHiddenPackageNames()
        val sortOrder = preferencesDataSource.getSortOrder()

        val appMap = LinkedHashMap<String, AppModel>()

        // 1. Process Leanback apps first (preferred for Android TV)
        for (info in leanbackApps) {
            val pkg = info.activityInfo.packageName
            val activity = info.activityInfo.name
            if (pkg == selfPackageName) continue // Don't show LeanTV itself inside the launcher

            val key = "$pkg/$activity"
            val label = info.loadLabel(packageManager)?.toString() ?: pkg
            val isFav = favorites.contains(key)
            val isHid = hiddenApps.contains(key)

            if (!includeHidden && isHid) continue

            val (firstInstall, lastUpdate) = getPackageTimestamps(pkg)

            appMap[key] = AppModel(
                label = label,
                packageName = pkg,
                activityName = activity,
                isLeanback = true,
                isFavorite = isFav,
                isHidden = isHid,
                firstInstallTime = firstInstall,
                lastUpdateTime = lastUpdate
            )
        }

        // 2. Process standard launcher apps (e.g. sideloaded apps, settings)
        for (info in standardApps) {
            val pkg = info.activityInfo.packageName
            val activity = info.activityInfo.name
            if (pkg == selfPackageName) continue

            val key = "$pkg/$activity"
            // If already added as Leanback, keep the Leanback version
            if (appMap.containsKey(key)) continue

            val isHid = hiddenApps.contains(key)
            if (!includeHidden && isHid) continue

            val label = info.loadLabel(packageManager)?.toString() ?: pkg
            val isFav = favorites.contains(key)
            val (firstInstall, lastUpdate) = getPackageTimestamps(pkg)

            appMap[key] = AppModel(
                label = label,
                packageName = pkg,
                activityName = activity,
                isLeanback = false,
                isFavorite = isFav,
                isHidden = isHid,
                firstInstallTime = firstInstall,
                lastUpdateTime = lastUpdate
            )
        }

        val appList = appMap.values.toList()
        sortAppList(appList, sortOrder)
    }

    private fun queryActivities(intent: Intent, flags: Int): List<ResolveInfo> {
        return try {
            packageManager.queryIntentActivities(intent, flags)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getPackageTimestamps(packageName: String): Pair<Long, Long> {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            Pair(packageInfo.firstInstallTime, packageInfo.lastUpdateTime)
        } catch (e: Exception) {
            Pair(0L, 0L)
        }
    }

    private fun sortAppList(list: List<AppModel>, sortOrder: String): List<AppModel> {
        return when (sortOrder) {
            AppConstants.SORT_ZA -> list.sortedByDescending { it.label.lowercase() }
            AppConstants.SORT_RECENT -> list.sortedByDescending { it.lastUpdateTime }
            AppConstants.SORT_AZ -> list.sortedBy { it.label.lowercase() }
            else -> list.sortedBy { it.label.lowercase() }
        }
    }

    /**
     * Load an app icon or TV banner asynchronously.
     * Uses in-memory LRU cache to avoid re-resolving or disk decoding on every scroll.
     */
    suspend fun loadAppIcon(app: AppModel): Drawable? = withContext(Dispatchers.IO) {
        val cacheKey = app.componentKey
        val cached = MemoryCache.get(cacheKey)
        if (cached != null) return@withContext cached

        var drawable: Drawable? = null
        try {
            // Check for TV Leanback banner if available
            val banner = packageManager.getActivityBanner(
                android.content.ComponentName(app.packageName, app.activityName)
            )
            drawable = banner ?: packageManager.getApplicationBanner(app.packageName)

            // Fallback to app activity icon or application icon
            if (drawable == null) {
                drawable = packageManager.getActivityIcon(
                    android.content.ComponentName(app.packageName, app.activityName)
                )
            }
        } catch (e: Exception) {
            // Fallback to application default icon
            try {
                drawable = packageManager.getApplicationIcon(app.packageName)
            } catch (ignored: Exception) {}
        }

        if (drawable != null) {
            MemoryCache.put(cacheKey, drawable)
        }
        drawable
    }

    suspend fun toggleFavorite(componentKey: String): Boolean = withContext(Dispatchers.IO) {
        preferencesDataSource.toggleFavorite(componentKey)
    }

    suspend fun setAppHidden(componentKey: String, isHidden: Boolean) = withContext(Dispatchers.IO) {
        preferencesDataSource.setAppHidden(componentKey, isHidden)
    }

    fun clearIconCache() {
        MemoryCache.clear()
    }

    fun evictIcon(componentKey: String) {
        MemoryCache.remove(componentKey)
    }
}

