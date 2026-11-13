package com.auratv.launcher.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
enum class ThemeMode {
    DARK, AMOLED, LIGHT, AUTO
}

@Serializable
enum class CornerRadiusStyle {
    SHARP, ROUNDED, PILL
}

@Serializable
enum class LayoutStyle {
    GRID, SINGLE_ROW, CATEGORIES, CLASSIC_LIST
}

@Serializable
enum class CardSize {
    SMALL, MEDIUM, LARGE
}

@Serializable
enum class AutoSortOption {
    ALPHABETICAL, MOST_USED, RECENTLY_INSTALLED, MANUAL
}

@Serializable
enum class WallpaperType {
    SOLID, GRADIENT, IMAGE, SLIDESHOW
}

@Serializable
enum class IconStyle {
    BANNER, SQUARE, CIRCLE
}

@Serializable
enum class FontFamilyChoice {
    DEFAULT, SANS_SERIF, SERIF, MONOSPACE, ROUNDED
}

@Serializable
enum class TopBarPosition {
    TOP, BOTTOM, HIDDEN
}

@Serializable
enum class FocusEffect {
    SCALE, GLOW, BORDER, PARALLAX, NONE
}

@Serializable
enum class AnimationSpeed {
    OFF, FAST, NORMAL, SLOW
}

@Serializable
enum class RemoteShortcutAction {
    NONE, SETTINGS, LIVE_TV, BROWSER, RECENT_APPS, CUSTOM_APP
}

@Serializable
data class CustomRow(
    val id: String,
    val title: String,
    val packageNames: List<String> = emptyList()
)

@Serializable
@Entity(tableName = "app_preferences")
data class AppItem(
    @PrimaryKey val packageName: String,
    val activityName: String,
    val label: String,
    val customLabel: String? = null,
    val isLeanback: Boolean = true,
    val bannerPath: String? = null,
    val iconPath: String? = null,
    val customIconPath: String? = null,
    val dominantColor: Int = 0xFF1E293B.toInt(),
    val isFavorite: Boolean = false,
    val favoriteOrder: Int = Int.MAX_VALUE,
    val isHidden: Boolean = false,
    val lastLaunchTime: Long = 0L,
    val launchCount: Int = 0,
    val installTime: Long = 0L,
    val category: String = "Other"
) {
    val displayLabel: String get() = customLabel?.takeIf { it.isNotBlank() } ?: label
    val effectiveIconPath: String? get() = customIconPath ?: iconPath
}

@Serializable
data class LauncherProfile(
    val name: String,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val accentColorHex: String = "#6366F1",
    val cornerRadiusStyle: CornerRadiusStyle = CornerRadiusStyle.ROUNDED,
    val layoutStyle: LayoutStyle = LayoutStyle.GRID,
    val gridColumns: Int = 5,
    val cardSize: CardSize = CardSize.MEDIUM,
    val showFavorites: Boolean = true,
    val showRecents: Boolean = true,
    val showAllApps: Boolean = true,
    val showTopBar: Boolean = true,
    val sectionOrder: List<String> = listOf("favorites", "recents", "categories", "all_apps"),
    val autoSort: AutoSortOption = AutoSortOption.ALPHABETICAL,
    val wallpaperType: WallpaperType = WallpaperType.SOLID,
    val wallpaperColor: String = "#0A0C14",
    val wallpaperPath: String? = null,
    val wallpaperFolderPath: String? = null,
    val wallpaperBlur: Float = 0.5f,
    val wallpaperDim: Float = 0.5f,
    val iconStyle: IconStyle = IconStyle.BANNER,
    val customIconPackPackage: String? = null,
    val fontFamily: FontFamilyChoice = FontFamilyChoice.DEFAULT,
    val textSizeScale: Float = 1.0f,
    val showAppLabels: Boolean = true,
    val clockFormat24h: Boolean = false,
    val showSeconds: Boolean = false,
    val dateFormatPattern: String = "EEE, MMM d",
    val showWeather: Boolean = false,
    val showWifiIndicator: Boolean = true,
    val showBluetoothIndicator: Boolean = true,
    val showUsbIndicator: Boolean = true,
    val showStorageIndicator: Boolean = true,
    val topBarPosition: TopBarPosition = TopBarPosition.TOP,
    val focusEffect: FocusEffect = FocusEffect.SCALE,
    val animationSpeed: AnimationSpeed = AnimationSpeed.NORMAL,
    val soundEffectsEnabled: Boolean = true,
    val autoLaunchBootPackage: String? = null,
    val longPressHomeAction: RemoteShortcutAction = RemoteShortcutAction.SETTINGS,
    val remoteColorButtonPackage: String? = null,
    val pinProtectedSettings: Boolean = false,
    val settingsPin: String = "",
    val customRows: List<CustomRow> = emptyList()
)
