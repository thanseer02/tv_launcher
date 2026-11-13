package com.auratv.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.auratv.launcher.model.AnimationSpeed
import com.auratv.launcher.model.AutoSortOption
import com.auratv.launcher.model.CardSize
import com.auratv.launcher.model.CornerRadiusStyle
import com.auratv.launcher.model.CustomRow
import com.auratv.launcher.model.FocusEffect
import com.auratv.launcher.model.FontFamilyChoice
import com.auratv.launcher.model.IconStyle
import com.auratv.launcher.model.LayoutStyle
import com.auratv.launcher.model.LauncherProfile
import com.auratv.launcher.model.RemoteShortcutAction
import com.auratv.launcher.model.ThemeMode
import com.auratv.launcher.model.TopBarPosition
import com.auratv.launcher.model.WallpaperType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "aura_tv_datastore")

class LauncherDataStoreRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_COLOR_HEX = stringPreferencesKey("accent_color_hex")
        val EXTRACT_ACCENT_FROM_WALLPAPER = booleanPreferencesKey("extract_accent_from_wallpaper")
        val CORNER_RADIUS_STYLE = stringPreferencesKey("corner_radius_style")

        val LAYOUT_STYLE = stringPreferencesKey("layout_style")
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        val CARD_SIZE = stringPreferencesKey("card_size")
        val SHOW_FAVORITES = booleanPreferencesKey("show_favorites")
        val SHOW_RECENTS = booleanPreferencesKey("show_recents")
        val SHOW_ALL_APPS = booleanPreferencesKey("show_all_apps")
        val SHOW_TOP_BAR = booleanPreferencesKey("show_top_bar")
        val SECTION_ORDER_JSON = stringPreferencesKey("section_order_json")
        val AUTO_SORT = stringPreferencesKey("auto_sort")
        val CUSTOM_ROWS_JSON = stringPreferencesKey("custom_rows_json")

        val WALLPAPER_TYPE = stringPreferencesKey("wallpaper_type")
        val WALLPAPER_COLOR = stringPreferencesKey("wallpaper_color")
        val WALLPAPER_PATH = stringPreferencesKey("wallpaper_path")
        val WALLPAPER_FOLDER_PATH = stringPreferencesKey("wallpaper_folder_path")
        val WALLPAPER_BLUR = floatPreferencesKey("wallpaper_blur")
        val WALLPAPER_DIM = floatPreferencesKey("wallpaper_dim")

        val ICON_STYLE = stringPreferencesKey("icon_style")
        val ICON_PACK_PACKAGE = stringPreferencesKey("icon_pack_package")
        val FONT_FAMILY = stringPreferencesKey("font_family")
        val TEXT_SIZE_SCALE = floatPreferencesKey("text_size_scale")
        val SHOW_APP_LABELS = booleanPreferencesKey("show_app_labels")

        val CLOCK_24H = booleanPreferencesKey("clock_24h")
        val SHOW_SECONDS = booleanPreferencesKey("show_seconds")
        val DATE_FORMAT_PATTERN = stringPreferencesKey("date_format_pattern")
        val SHOW_WEATHER = booleanPreferencesKey("show_weather")
        val SHOW_WIFI = booleanPreferencesKey("show_wifi")
        val SHOW_BLUETOOTH = booleanPreferencesKey("show_bluetooth")
        val SHOW_USB = booleanPreferencesKey("show_usb")
        val SHOW_STORAGE = booleanPreferencesKey("show_storage")
        val TOP_BAR_POSITION = stringPreferencesKey("top_bar_position")

        val FOCUS_EFFECT = stringPreferencesKey("focus_effect")
        val ANIMATION_SPEED = stringPreferencesKey("animation_speed")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")

        val AUTO_LAUNCH_BOOT_PKG = stringPreferencesKey("auto_launch_boot_pkg")
        val LONG_PRESS_HOME_ACTION = stringPreferencesKey("long_press_home_action")
        val REMOTE_COLOR_BUTTON_PKG = stringPreferencesKey("remote_color_button_pkg")
        val PIN_PROTECTED_SETTINGS = booleanPreferencesKey("pin_protected_settings")
        val SETTINGS_PIN = stringPreferencesKey("settings_pin")
    }

    val profileFlow: Flow<LauncherProfile> = context.dataStore.data.map { prefs ->
        mapPreferencesToProfile(prefs)
    }

    private fun mapPreferencesToProfile(prefs: Preferences): LauncherProfile {
        val customRowsJson = prefs[Keys.CUSTOM_ROWS_JSON]
        val customRows = if (!customRowsJson.isNullOrEmpty()) {
            try { json.decodeFromString<List<CustomRow>>(customRowsJson) } catch (e: Exception) { emptyList() }
        } else emptyList()

        val sectionOrderJson = prefs[Keys.SECTION_ORDER_JSON]
        val sectionOrder = if (!sectionOrderJson.isNullOrEmpty()) {
            try { json.decodeFromString<List<String>>(sectionOrderJson) } catch (e: Exception) {
                listOf("favorites", "recents", "categories", "all_apps")
            }
        } else listOf("favorites", "recents", "categories", "all_apps")

        return LauncherProfile(
            name = "Active",
            themeMode = parseEnum(prefs[Keys.THEME_MODE], ThemeMode.DARK),
            accentColorHex = prefs[Keys.ACCENT_COLOR_HEX] ?: "#6366F1",
            cornerRadiusStyle = parseEnum(prefs[Keys.CORNER_RADIUS_STYLE], CornerRadiusStyle.ROUNDED),
            layoutStyle = parseEnum(prefs[Keys.LAYOUT_STYLE], LayoutStyle.GRID),
            gridColumns = prefs[Keys.GRID_COLUMNS] ?: 5,
            cardSize = parseEnum(prefs[Keys.CARD_SIZE], CardSize.MEDIUM),
            showFavorites = prefs[Keys.SHOW_FAVORITES] ?: true,
            showRecents = prefs[Keys.SHOW_RECENTS] ?: true,
            showAllApps = prefs[Keys.SHOW_ALL_APPS] ?: true,
            showTopBar = prefs[Keys.SHOW_TOP_BAR] ?: true,
            sectionOrder = sectionOrder,
            autoSort = parseEnum(prefs[Keys.AUTO_SORT], AutoSortOption.ALPHABETICAL),
            wallpaperType = parseEnum(prefs[Keys.WALLPAPER_TYPE], WallpaperType.SOLID),
            wallpaperColor = prefs[Keys.WALLPAPER_COLOR] ?: "#0A0C14",
            wallpaperPath = prefs[Keys.WALLPAPER_PATH],
            wallpaperFolderPath = prefs[Keys.WALLPAPER_FOLDER_PATH],
            wallpaperBlur = prefs[Keys.WALLPAPER_BLUR] ?: 0.5f,
            wallpaperDim = prefs[Keys.WALLPAPER_DIM] ?: 0.5f,
            iconStyle = parseEnum(prefs[Keys.ICON_STYLE], IconStyle.BANNER),
            customIconPackPackage = prefs[Keys.ICON_PACK_PACKAGE],
            fontFamily = parseEnum(prefs[Keys.FONT_FAMILY], FontFamilyChoice.DEFAULT),
            textSizeScale = prefs[Keys.TEXT_SIZE_SCALE] ?: 1.0f,
            showAppLabels = prefs[Keys.SHOW_APP_LABELS] ?: true,
            clockFormat24h = prefs[Keys.CLOCK_24H] ?: false,
            showSeconds = prefs[Keys.SHOW_SECONDS] ?: false,
            dateFormatPattern = prefs[Keys.DATE_FORMAT_PATTERN] ?: "EEE, MMM d",
            showWeather = prefs[Keys.SHOW_WEATHER] ?: false,
            showWifiIndicator = prefs[Keys.SHOW_WIFI] ?: true,
            showBluetoothIndicator = prefs[Keys.SHOW_BLUETOOTH] ?: true,
            showUsbIndicator = prefs[Keys.SHOW_USB] ?: true,
            showStorageIndicator = prefs[Keys.SHOW_STORAGE] ?: true,
            topBarPosition = parseEnum(prefs[Keys.TOP_BAR_POSITION], TopBarPosition.TOP),
            focusEffect = parseEnum(prefs[Keys.FOCUS_EFFECT], FocusEffect.SCALE),
            animationSpeed = parseEnum(prefs[Keys.ANIMATION_SPEED], AnimationSpeed.NORMAL),
            soundEffectsEnabled = prefs[Keys.SOUND_EFFECTS] ?: true,
            autoLaunchBootPackage = prefs[Keys.AUTO_LAUNCH_BOOT_PKG],
            longPressHomeAction = parseEnum(prefs[Keys.LONG_PRESS_HOME_ACTION], RemoteShortcutAction.SETTINGS),
            remoteColorButtonPackage = prefs[Keys.REMOTE_COLOR_BUTTON_PKG],
            pinProtectedSettings = prefs[Keys.PIN_PROTECTED_SETTINGS] ?: false,
            settingsPin = prefs[Keys.SETTINGS_PIN] ?: "",
            customRows = customRows
        )
    }

    private inline fun <reified T : Enum<T>> parseEnum(value: String?, default: T): T {
        if (value == null) return default
        return try {
            java.lang.Enum.valueOf(T::class.java, value)
        } catch (e: Exception) {
            default
        }
    }

    suspend fun saveProfile(profile: LauncherProfile) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = profile.themeMode.name
            prefs[Keys.ACCENT_COLOR_HEX] = profile.accentColorHex
            prefs[Keys.CORNER_RADIUS_STYLE] = profile.cornerRadiusStyle.name

            prefs[Keys.LAYOUT_STYLE] = profile.layoutStyle.name
            prefs[Keys.GRID_COLUMNS] = profile.gridColumns.coerceIn(4, 8)
            prefs[Keys.CARD_SIZE] = profile.cardSize.name
            prefs[Keys.SHOW_FAVORITES] = profile.showFavorites
            prefs[Keys.SHOW_RECENTS] = profile.showRecents
            prefs[Keys.SHOW_ALL_APPS] = profile.showAllApps
            prefs[Keys.SHOW_TOP_BAR] = profile.showTopBar
            prefs[Keys.SECTION_ORDER_JSON] = json.encodeToString(profile.sectionOrder)
            prefs[Keys.AUTO_SORT] = profile.autoSort.name
            prefs[Keys.CUSTOM_ROWS_JSON] = json.encodeToString(profile.customRows)

            prefs[Keys.WALLPAPER_TYPE] = profile.wallpaperType.name
            prefs[Keys.WALLPAPER_COLOR] = profile.wallpaperColor
            if (profile.wallpaperPath != null) prefs[Keys.WALLPAPER_PATH] = profile.wallpaperPath else prefs.remove(Keys.WALLPAPER_PATH)
            if (profile.wallpaperFolderPath != null) prefs[Keys.WALLPAPER_FOLDER_PATH] = profile.wallpaperFolderPath else prefs.remove(Keys.WALLPAPER_FOLDER_PATH)
            prefs[Keys.WALLPAPER_BLUR] = profile.wallpaperBlur
            prefs[Keys.WALLPAPER_DIM] = profile.wallpaperDim

            prefs[Keys.ICON_STYLE] = profile.iconStyle.name
            if (profile.customIconPackPackage != null) prefs[Keys.ICON_PACK_PACKAGE] = profile.customIconPackPackage else prefs.remove(Keys.ICON_PACK_PACKAGE)
            prefs[Keys.FONT_FAMILY] = profile.fontFamily.name
            prefs[Keys.TEXT_SIZE_SCALE] = profile.textSizeScale
            prefs[Keys.SHOW_APP_LABELS] = profile.showAppLabels

            prefs[Keys.CLOCK_24H] = profile.clockFormat24h
            prefs[Keys.SHOW_SECONDS] = profile.showSeconds
            prefs[Keys.DATE_FORMAT_PATTERN] = profile.dateFormatPattern
            prefs[Keys.SHOW_WEATHER] = profile.showWeather
            prefs[Keys.SHOW_WIFI] = profile.showWifiIndicator
            prefs[Keys.SHOW_BLUETOOTH] = profile.showBluetoothIndicator
            prefs[Keys.SHOW_USB] = profile.showUsbIndicator
            prefs[Keys.SHOW_STORAGE] = profile.showStorageIndicator
            prefs[Keys.TOP_BAR_POSITION] = profile.topBarPosition.name

            prefs[Keys.FOCUS_EFFECT] = profile.focusEffect.name
            prefs[Keys.ANIMATION_SPEED] = profile.animationSpeed.name
            prefs[Keys.SOUND_EFFECTS] = profile.soundEffectsEnabled

            if (profile.autoLaunchBootPackage != null) prefs[Keys.AUTO_LAUNCH_BOOT_PKG] = profile.autoLaunchBootPackage else prefs.remove(Keys.AUTO_LAUNCH_BOOT_PKG)
            prefs[Keys.LONG_PRESS_HOME_ACTION] = profile.longPressHomeAction.name
            if (profile.remoteColorButtonPackage != null) prefs[Keys.REMOTE_COLOR_BUTTON_PKG] = profile.remoteColorButtonPackage else prefs.remove(Keys.REMOTE_COLOR_BUTTON_PKG)
            prefs[Keys.PIN_PROTECTED_SETTINGS] = profile.pinProtectedSettings
            prefs[Keys.SETTINGS_PIN] = profile.settingsPin
        }
    }

    suspend fun resetToDefault() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun exportJson(profile: LauncherProfile): String {
        return json.encodeToString(profile)
    }

    suspend fun importJson(jsonString: String): LauncherProfile {
        val profile = json.decodeFromString<LauncherProfile>(jsonString)
        saveProfile(profile)
        return profile
    }
}

