package com.auratv.launcher.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("aura_tv_settings", Context.MODE_PRIVATE)

    private val _gridColumnCount = MutableStateFlow(prefs.getInt(KEY_GRID_COLUMNS, 5))
    val gridColumnCount: StateFlow<Int> = _gridColumnCount.asStateFlow()

    private val _wallpaperPath = MutableStateFlow(prefs.getString(KEY_WALLPAPER_PATH, null))
    val wallpaperPath: StateFlow<String?> = _wallpaperPath.asStateFlow()

    fun setGridColumnCount(count: Int) {
        val clamped = count.coerceIn(3, 8)
        prefs.edit { putInt(KEY_GRID_COLUMNS, clamped) }
        _gridColumnCount.value = clamped
    }

    fun setWallpaperPath(path: String?) {
        prefs.edit { putString(KEY_WALLPAPER_PATH, path) }
        _wallpaperPath.value = path
    }

    companion object {
        private const val KEY_GRID_COLUMNS = "grid_columns"
        private const val KEY_WALLPAPER_PATH = "wallpaper_path"
    }
}

