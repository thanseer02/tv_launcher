package com.auratv.launcher.model

object BuiltinProfiles {
    val FREE_MY_TV = LauncherProfile(
        name = "Free My TV (Glass)",
        themeMode = ThemeMode.DARK,
        accentColorHex = "#FFFFFF",
        cornerRadiusStyle = CornerRadiusStyle.ROUNDED,
        layoutStyle = LayoutStyle.SINGLE_ROW,
        gridColumns = 7,
        cardSize = CardSize.MEDIUM,
        showFavorites = true,
        showRecents = false,
        showAllApps = true,
        showTopBar = true,
        showAppLabels = false,
        iconStyle = IconStyle.BANNER,
        wallpaperType = WallpaperType.GRADIENT,
        wallpaperColor = "#0D0E15",
        wallpaperBlur = 0.8f,
        wallpaperDim = 0.45f,
        focusEffect = FocusEffect.SCALE,
        animationSpeed = AnimationSpeed.FAST
    )

    val MINIMAL = LauncherProfile(
        name = "Minimal",
        themeMode = ThemeMode.AMOLED,
        accentColorHex = "#A1A1AA",
        cornerRadiusStyle = CornerRadiusStyle.SHARP,
        layoutStyle = LayoutStyle.SINGLE_ROW,
        gridColumns = 6,
        cardSize = CardSize.SMALL,
        showFavorites = true,
        showRecents = false,
        showAllApps = true,
        showTopBar = true,
        showAppLabels = false,
        iconStyle = IconStyle.SQUARE,
        wallpaperType = WallpaperType.SOLID,
        wallpaperColor = "#000000",
        wallpaperDim = 0.8f,
        focusEffect = FocusEffect.BORDER,
        animationSpeed = AnimationSpeed.FAST
    )

    val MEDIA_CENTER = LauncherProfile(
        name = "Media Center",
        themeMode = ThemeMode.DARK,
        accentColorHex = "#E50914", // Cinematic red
        cornerRadiusStyle = CornerRadiusStyle.ROUNDED,
        layoutStyle = LayoutStyle.GRID,
        gridColumns = 5,
        cardSize = CardSize.LARGE,
        showFavorites = true,
        showRecents = true,
        showAllApps = true,
        showTopBar = true,
        iconStyle = IconStyle.BANNER,
        wallpaperType = WallpaperType.GRADIENT,
        wallpaperColor = "#0F172A",
        wallpaperBlur = 0.7f,
        wallpaperDim = 0.4f,
        focusEffect = FocusEffect.GLOW,
        animationSpeed = AnimationSpeed.NORMAL
    )

    val GAMING = LauncherProfile(
        name = "Gaming",
        themeMode = ThemeMode.AMOLED,
        accentColorHex = "#10B981", // High-contrast emerald
        cornerRadiusStyle = CornerRadiusStyle.ROUNDED,
        layoutStyle = LayoutStyle.GRID,
        gridColumns = 5,
        cardSize = CardSize.MEDIUM,
        showFavorites = true,
        showRecents = true,
        showAllApps = true,
        showTopBar = true,
        iconStyle = IconStyle.BANNER,
        wallpaperType = WallpaperType.SOLID,
        wallpaperColor = "#05050A",
        focusEffect = FocusEffect.SCALE,
        animationSpeed = AnimationSpeed.FAST
    )

    val KIDS = LauncherProfile(
        name = "Kids",
        themeMode = ThemeMode.LIGHT,
        accentColorHex = "#F59E0B", // Bright Amber
        cornerRadiusStyle = CornerRadiusStyle.PILL,
        layoutStyle = LayoutStyle.CATEGORIES,
        gridColumns = 4,
        cardSize = CardSize.LARGE,
        showFavorites = true,
        showRecents = false,
        showAllApps = true,
        showTopBar = false,
        iconStyle = IconStyle.CIRCLE,
        wallpaperType = WallpaperType.SOLID,
        wallpaperColor = "#FEF3C7",
        pinProtectedSettings = true,
        settingsPin = "1234",
        focusEffect = FocusEffect.SCALE,
        animationSpeed = AnimationSpeed.SLOW
    )

    val ALL_PRESETS = listOf(FREE_MY_TV, MINIMAL, MEDIA_CENTER, GAMING, KIDS)
}

