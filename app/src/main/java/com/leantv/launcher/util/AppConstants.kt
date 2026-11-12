package com.leantv.launcher.util

object AppConstants {
    const val PREFS_NAME = "leantv_preferences"

    // Preferences keys
    const val KEY_FAVORITES = "pref_favorites_set"
    const val KEY_HIDDEN_APPS = "pref_hidden_apps_set"
    const val KEY_SORT_ORDER = "pref_sort_order"
    const val KEY_GRID_COLUMNS = "pref_grid_columns"
    const val KEY_THEME = "pref_theme"

    // Sort types
    const val SORT_AZ = "AZ"
    const val SORT_ZA = "ZA"
    const val SORT_RECENT = "RECENT"

    // Themes
    const val THEME_OLED = "OLED"
    const val THEME_CHARCOAL = "CHARCOAL"
    const val THEME_DEEP_NAVY = "DEEP_NAVY"

    // Default configuration for 1080p/4K TV screens
    const val DEFAULT_GRID_COLUMNS = 5
    const val MIN_GRID_COLUMNS = 4
    const val MAX_GRID_COLUMNS = 6

    // Max cache size in bytes (8 MB max for app icons)
    const val MAX_ICON_CACHE_BYTES = 8 * 1024 * 1024
}

