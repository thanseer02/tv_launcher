package com.leantv.launcher.data.source

import android.content.Context
import android.content.SharedPreferences
import com.leantv.launcher.util.AppConstants

/**
 * SharedPreferences-backed fast data source.
 * Thread-safe, non-blocking disk persistence with zero Room/SQLite runtime overhead.
 */
class PreferencesDataSource(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)

    fun getFavoritePackageNames(): Set<String> {
        return prefs.getStringSet(AppConstants.KEY_FAVORITES, emptySet())?.toSet() ?: emptySet()
    }

    fun addFavorite(componentKey: String) {
        val current = getFavoritePackageNames().toMutableSet()
        current.add(componentKey)
        prefs.edit().putStringSet(AppConstants.KEY_FAVORITES, current).apply()
    }

    fun removeFavorite(componentKey: String) {
        val current = getFavoritePackageNames().toMutableSet()
        current.remove(componentKey)
        prefs.edit().putStringSet(AppConstants.KEY_FAVORITES, current).apply()
    }

    fun toggleFavorite(componentKey: String): Boolean {
        val current = getFavoritePackageNames().toMutableSet()
        val willBeFavorite = !current.contains(componentKey)
        if (willBeFavorite) {
            current.add(componentKey)
        } else {
            current.remove(componentKey)
        }
        prefs.edit().putStringSet(AppConstants.KEY_FAVORITES, current).apply()
        return willBeFavorite
    }

    fun getHiddenPackageNames(): Set<String> {
        return prefs.getStringSet(AppConstants.KEY_HIDDEN_APPS, emptySet())?.toSet() ?: emptySet()
    }

    fun setAppHidden(componentKey: String, isHidden: Boolean) {
        val current = getHiddenPackageNames().toMutableSet()
        if (isHidden) {
            current.add(componentKey)
        } else {
            current.remove(componentKey)
        }
        prefs.edit().putStringSet(AppConstants.KEY_HIDDEN_APPS, current).apply()
    }

    fun getSortOrder(): String {
        return prefs.getString(AppConstants.KEY_SORT_ORDER, AppConstants.SORT_AZ) ?: AppConstants.SORT_AZ
    }

    fun setSortOrder(order: String) {
        prefs.edit().putString(AppConstants.KEY_SORT_ORDER, order).apply()
    }

    fun getGridColumns(): Int {
        return prefs.getInt(AppConstants.KEY_GRID_COLUMNS, AppConstants.DEFAULT_GRID_COLUMNS)
    }

    fun setGridColumns(columns: Int) {
        val safeColumns = columns.coerceIn(AppConstants.MIN_GRID_COLUMNS, AppConstants.MAX_GRID_COLUMNS)
        prefs.edit().putInt(AppConstants.KEY_GRID_COLUMNS, safeColumns).apply()
    }

    fun getTheme(): String {
        return prefs.getString(AppConstants.KEY_THEME, AppConstants.THEME_OLED) ?: AppConstants.THEME_OLED
    }

    fun setTheme(theme: String) {
        prefs.edit().putString(AppConstants.KEY_THEME, theme).apply()
    }
}

