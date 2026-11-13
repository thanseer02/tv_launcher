package com.auratv.launcher

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.auratv.launcher.model.AppItem
import com.auratv.launcher.model.RemoteShortcutAction
import com.auratv.launcher.ui.AppActionDialog
import com.auratv.launcher.ui.HomeScreen
import com.auratv.launcher.ui.SettingsDialog
import com.auratv.launcher.ui.parseHexColor
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val profile by viewModel.profile.collectAsState()
            val visibleApps by viewModel.visibleApps.collectAsState()
            val allApps by viewModel.allApps.collectAsState()
            val favorites by viewModel.favoriteApps.collectAsState()
            val recentApps by viewModel.recentApps.collectAsState()
            val selectedAppForMenu by viewModel.selectedAppForMenu.collectAsState()
            val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
            val isReorderingFavorites by viewModel.isReorderingFavorites.collectAsState()

            val accentColor = parseHexColor(profile.accentColorHex)

            // Wallpaper image picker launcher
            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                if (uri != null) {
                    try {
                        val wallpaperFile = File(filesDir, "custom_wallpaper_${System.currentTimeMillis()}.jpg")
                        contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(wallpaperFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        viewModel.updateProfile(profile.copy(
                            wallpaperType = com.auratv.launcher.model.WallpaperType.IMAGE,
                            wallpaperPath = wallpaperFile.absolutePath
                        ))
                        Toast.makeText(this, "Wallpaper applied", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Custom app icon picker launcher
            var currentAppForIconPick: AppItem? = null
            val iconPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                val targetApp = currentAppForIconPick
                if (uri != null && targetApp != null) {
                    try {
                        val iconFile = File(filesDir, "custom_icon_${targetApp.packageName}.png")
                        contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(iconFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        viewModel.setAppCustomIcon(targetApp, iconFile.absolutePath)
                        Toast.makeText(this, "Custom icon applied", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Failed to set icon", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Remote Back button interceptor
            BackHandler(enabled = isSettingsOpen || selectedAppForMenu != null || isReorderingFavorites) {
                when {
                    selectedAppForMenu != null -> viewModel.closeAppMenu()
                    isSettingsOpen -> viewModel.closeSettings()
                    isReorderingFavorites -> viewModel.finishReorderingFavorites()
                }
            }

            // Main HomeScreen
            HomeScreen(
                profile = profile,
                favorites = favorites,
                recentApps = recentApps,
                allApps = visibleApps,
                accentColor = accentColor,
                isReorderingFavorites = isReorderingFavorites,
                onAppClick = { app -> launchApp(app) },
                onAppLongClick = { app -> viewModel.openAppMenu(app) },
                onMoveFavoriteLeft = { app -> viewModel.moveFavorite(app, -1) },
                onMoveFavoriteRight = { app -> viewModel.moveFavorite(app, 1) },
                onFinishReordering = { viewModel.finishReorderingFavorites() },
                onOpenSettings = { viewModel.openSettings() },
                modifier = Modifier.fillMaxSize()
            )

            // Settings Dialog (rendered above HomeScreen)
            SettingsDialog(
                isOpen = isSettingsOpen,
                profile = profile,
                allApps = allApps,
                accentColor = accentColor,
                onDismiss = { viewModel.closeSettings() },
                onUpdateProfile = { updated -> viewModel.updateProfile(updated) },
                onToggleHideApp = { app -> viewModel.toggleHideApp(app) },
                onResetDefaults = {
                    viewModel.resetDefaults()
                    Toast.makeText(this, "Reset to defaults", Toast.LENGTH_SHORT).show()
                },
                onExportBackup = {
                    viewModel.exportBackup { json ->
                        try {
                            val backupFile = File(getExternalFilesDir(null), "aura_tv_profile.json")
                            backupFile.writeText(json)
                            Toast.makeText(this, "Exported to ${backupFile.name}", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onImportBackup = {
                    try {
                        val backupFile = File(getExternalFilesDir(null), "aura_tv_profile.json")
                        if (backupFile.exists()) {
                            viewModel.importBackup(backupFile.readText()) {
                                Toast.makeText(this, "Settings restored", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "No backup file found at ${backupFile.name}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Failed to restore: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                onPickWallpaper = {
                    imagePickerLauncher.launch("image/*")
                },
                onPickWallpaperFolder = {
                    Toast.makeText(this, "Place images in /sdcard/Pictures/Wallpapers", Toast.LENGTH_LONG).show()
                    val folder = File("/sdcard/Pictures/Wallpapers")
                    if (!folder.exists()) folder.mkdirs()
                    viewModel.updateProfile(profile.copy(
                        wallpaperType = com.auratv.launcher.model.WallpaperType.SLIDESHOW,
                        wallpaperFolderPath = folder.absolutePath
                    ))
                }
            )

            // Context Menu Dialog (Long-Press / Hold OK)
            AppActionDialog(
                app = selectedAppForMenu,
                accentColor = accentColor,
                onDismiss = { viewModel.closeAppMenu() },
                onTogglePin = { app -> viewModel.toggleFavorite(app) },
                onHide = { app -> viewModel.toggleHideApp(app) },
                onUninstall = { app -> uninstallApp(app) },
                onAppInfo = { app -> openAppInfo(app) },
                onRename = { app, newName -> viewModel.renameApp(app, newName) },
                onPickCustomIcon = { app ->
                    currentAppForIconPick = app
                    iconPickerLauncher.launch("image/*")
                }
            )
        }
    }

    // Hardware and Remote Button Custom Interception
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val currentProfile = viewModel.profile.value

        // Long press Home key action mapping
        if (keyCode == KeyEvent.KEYCODE_HOME && event?.isLongPress == true) {
            when (currentProfile.longPressHomeAction) {
                RemoteShortcutAction.SETTINGS -> {
                    viewModel.openSettings()
                    return true
                }
                RemoteShortcutAction.LIVE_TV -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("content://android.media.tv/passthrough")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try { startActivity(intent); return true } catch (e: Exception) {}
                }
                RemoteShortcutAction.RECENT_APPS -> {
                    // Handled natively or toggle dialog
                }
                RemoteShortcutAction.CUSTOM_APP -> {
                    currentProfile.remoteColorButtonPackage?.let { pkg ->
                        packageManager.getLaunchIntentForPackage(pkg)?.let { startActivity(it); return true }
                    }
                }
                else -> {}
            }
        }

        // Color Button / Extra remote key mappings (e.g. Red, Green, Yellow, Blue, Guide)
        if (keyCode == KeyEvent.KEYCODE_PROG_RED || keyCode == KeyEvent.KEYCODE_GUIDE) {
            val targetPkg = currentProfile.remoteColorButtonPackage
            if (!targetPkg.isNullOrEmpty()) {
                val intent = packageManager.getLeanbackLaunchIntentForPackage(targetPkg)
                    ?: packageManager.getLaunchIntentForPackage(targetPkg)
                if (intent != null) {
                    startActivity(intent)
                    return true
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun launchApp(app: AppItem) {
        viewModel.recordAppLaunch(app)

        var intent: Intent? = null
        if (app.isLeanback) {
            intent = packageManager.getLeanbackLaunchIntentForPackage(app.packageName)
        }
        if (intent == null) {
            intent = packageManager.getLaunchIntentForPackage(app.packageName)
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to open ${app.displayLabel}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No launch activity found for ${app.displayLabel}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openAppInfo(app: AppItem) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${app.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Unable to open app details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uninstallApp(app: AppItem) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:${app.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to initiate uninstallation", Toast.LENGTH_SHORT).show()
        }
    }
}
