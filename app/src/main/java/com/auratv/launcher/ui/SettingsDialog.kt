package com.auratv.launcher.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.auratv.launcher.model.AnimationSpeed
import com.auratv.launcher.model.AppItem
import com.auratv.launcher.model.AutoSortOption
import com.auratv.launcher.model.BuiltinProfiles
import com.auratv.launcher.model.CardSize
import com.auratv.launcher.model.CornerRadiusStyle
import com.auratv.launcher.model.FocusEffect
import com.auratv.launcher.model.FontFamilyChoice
import com.auratv.launcher.model.IconStyle
import com.auratv.launcher.model.LayoutStyle
import com.auratv.launcher.model.LauncherProfile
import com.auratv.launcher.model.RemoteShortcutAction
import com.auratv.launcher.model.ThemeMode
import com.auratv.launcher.model.TopBarPosition
import com.auratv.launcher.model.WallpaperType

private val PRESET_ACCENTS = listOf(
    "#6366F1", // Indigo
    "#3B82F6", // Blue
    "#06B6D4", // Cyan
    "#10B981", // Emerald
    "#84CC16", // Lime
    "#F59E0B", // Amber
    "#EF4444", // Red
    "#EC4899", // Pink
    "#8B5CF6", // Purple
    "#14B8A6", // Teal
    "#F97316", // Orange
    "#E2E8F0"  // Slate White
)

@Composable
fun SettingsDialog(
    isOpen: Boolean,
    profile: LauncherProfile,
    allApps: List<AppItem>,
    accentColor: Color,
    onDismiss: () -> Unit,
    onUpdateProfile: (LauncherProfile) -> Unit,
    onToggleHideApp: (AppItem) -> Unit,
    onResetDefaults: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    onPickWallpaper: () -> Unit,
    onPickWallpaperFolder: () -> Unit,
    onSwitchToOriginalLauncher: () -> Unit
) {
    if (!isOpen) return

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn() + scaleIn(initialScale = 0.95f),
        exit = fadeOut() + scaleOut(targetScale = 0.95f),
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
    ) {
        var selectedCategory by remember { mutableStateOf(0) }
        var isPinVerified by remember { mutableStateOf(!profile.pinProtectedSettings) }
        var enteredPin by remember { mutableStateOf("") }

        val categories = listOf(
            "Presets",
            "Theme & Accent",
            "Layout & Cards",
            "Appearance",
            "Top Bar & Widgets",
            "Focus & Motion",
            "Behavior & Remote",
            "Hidden Apps",
            "Backup & Reset"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xEE090A0F))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!isPinVerified && profile.pinProtectedSettings) {
                // PIN Lock Challenge
                Column(
                    modifier = Modifier
                        .width(360.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF18181B))
                        .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = accentColor,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Settings Protected by PIN",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = { enteredPin = it },
                        label = { Text("Enter PIN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TabNavItem(
                            label = "Unlock",
                            isSelected = true,
                            accentColor = accentColor,
                            onClick = {
                                if (enteredPin == profile.settingsPin) {
                                    isPinVerified = true
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TabNavItem(
                            label = "Cancel",
                            isSelected = false,
                            accentColor = accentColor,
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // Split Screen: Left navigation categories, Center settings options, Right Live Preview
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF12141C))
                        .border(1.dp, Color(0xFF27272A), RoundedCornerShape(20.dp))
                ) {
                    // 1. Navigation Sidebar
                    LazyColumn(
                        modifier = Modifier
                            .width(220.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF0F1117))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            Text(
                                text = "Settings",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
                            )
                        }
                        items(categories.size) { index ->
                            TabNavItem(
                                label = categories[index],
                                isSelected = selectedCategory == index,
                                accentColor = accentColor,
                                onClick = { selectedCategory = index }
                            )
                        }
                    }

                    // 2. Settings Details Panel
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(24.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            when (selectedCategory) {
                                0 -> { // Presets
                                    item {
                                        SectionHeading("Preset Profiles", "Switch layout and style in one click")
                                    }
                                    items(BuiltinProfiles.ALL_PRESETS) { preset ->
                                        SettingClickableRow(
                                            title = preset.name,
                                            subtitle = "Apply ${preset.name} theme, layout and effects",
                                            accentColor = accentColor,
                                            onClick = { onUpdateProfile(preset) }
                                        )
                                    }
                                }
                                1 -> { // Theme & Colors
                                    item {
                                        SectionHeading("Theme Mode", "Dark, AMOLED Black, Light, or Time-based Auto")
                                        ChoiceRow(
                                            options = ThemeMode.values().map { it.name },
                                            selectedIndex = ThemeMode.values().indexOf(profile.themeMode),
                                            accentColor = accentColor,
                                            onSelect = { onUpdateProfile(profile.copy(themeMode = ThemeMode.values()[it])) }
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        SectionHeading("Accent Color", "Focus glow, borders, and indicators")
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        ) {
                                            items(PRESET_ACCENTS) { hex ->
                                                val c = parseHexColor(hex)
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(CircleShape)
                                                        .background(c)
                                                        .border(
                                                            width = if (profile.accentColorHex.equals(hex, ignoreCase = true)) 3.dp else 1.dp,
                                                            color = if (profile.accentColorHex.equals(hex, ignoreCase = true)) Color.White else Color(0x33FFFFFF),
                                                            shape = CircleShape
                                                        )
                                                        .clickable { onUpdateProfile(profile.copy(accentColorHex = hex)) },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (profile.accentColorHex.equals(hex, ignoreCase = true)) {
                                                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        SectionHeading("Corner Radius", "Sharp, Rounded or Pill cards")
                                        ChoiceRow(
                                            options = CornerRadiusStyle.values().map { it.name },
                                            selectedIndex = CornerRadiusStyle.values().indexOf(profile.cornerRadiusStyle),
                                            accentColor = accentColor,
                                            onSelect = { onUpdateProfile(profile.copy(cornerRadiusStyle = CornerRadiusStyle.values()[it])) }
                                        )
                                    }
                                }
                                2 -> { // Layout & Cards
                                    item {
                                        SectionHeading("Layout Style", "Grid, Single row, Categories, or Classic List")
                                        ChoiceRow(
                                            options = LayoutStyle.values().map { it.name },
                                            selectedIndex = LayoutStyle.values().indexOf(profile.layoutStyle),
                                            accentColor = accentColor,
                                            onSelect = { onUpdateProfile(profile.copy(layoutStyle = LayoutStyle.values()[it])) }
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SectionHeading("Grid Columns (${profile.gridColumns})", "Adjust grid columns from 4 to 8")
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            SettingsSmallButton(icon = Icons.Default.Remove, accentColor = accentColor, onClick = {
                                                if (profile.gridColumns > 4) onUpdateProfile(profile.copy(gridColumns = profile.gridColumns - 1))
                                            })
                                            SettingsSmallButton(icon = Icons.Default.Add, accentColor = accentColor, onClick = {
                                                if (profile.gridColumns < 8) onUpdateProfile(profile.copy(gridColumns = profile.gridColumns + 1))
                                            })
                                        }
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SectionHeading("Card Size", "Small, Medium, or Large cards")
                                        ChoiceRow(
                                            options = CardSize.values().map { it.name },
                                            selectedIndex = CardSize.values().indexOf(profile.cardSize),
                                            accentColor = accentColor,
                                            onSelect = { onUpdateProfile(profile.copy(cardSize = CardSize.values()[it])) }
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SectionHeading("Sections Toggle", "Choose which rows appear on home")
                                        ToggleRow("Show Favorites Row", profile.showFavorites, accentColor) {
                                            onUpdateProfile(profile.copy(showFavorites = it))
                                        }
                                        ToggleRow("Show Recent Apps Row", profile.showRecents, accentColor) {
                                            onUpdateProfile(profile.copy(showRecents = it))
                                        }
                                        ToggleRow("Show All Apps Section", profile.showAllApps, accentColor) {
                                            onUpdateProfile(profile.copy(showAllApps = it))
                                        }
                                        ToggleRow("Show Top Bar", profile.showTopBar, accentColor) {
                                            onUpdateProfile(profile.copy(showTopBar = it))
                                        }
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SectionHeading("Auto-Sort Apps", "Order for the main apps catalog")
                                        ChoiceRow(
                                            options = AutoSortOption.values().map { it.name },
                                            selectedIndex = AutoSortOption.values().indexOf(profile.autoSort),
                                            accentColor = accentColor,
                                            onSelect = { onUpdateProfile(profile.copy(autoSort = AutoSortOption.values()[it])) }
                                        )
                                    }
                                }
                                3 -> { // Appearance
                                    item {
                                        SectionHeading("Wallpaper Type", "Pick Solid, Gradient, Image or Slideshow")
                                        ChoiceRow(
                                            options = WallpaperType.values().map { it.name },
                                            selectedIndex = WallpaperType.values().indexOf(profile.wallpaperType),
                                            accentColor = accentColor,
                                            onSelect = { onUpdateProfile(profile.copy(wallpaperType = WallpaperType.values()[it])) }
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SettingClickableRow(
                                            title = "Choose Wallpaper Image",
                                            subtitle = profile.wallpaperPath ?: "Default theme background",
                                            accentColor = accentColor,
                                            onClick = onPickWallpaper
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        SettingClickableRow(
                                            title = "Choose Slideshow Folder",
                                            subtitle = profile.wallpaperFolderPath ?: "None selected",
                                            accentColor = accentColor,
                                            onClick = onPickWallpaperFolder
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SectionHeading("Wallpaper Dim (${(profile.wallpaperDim * 100).toInt()}%)", "Adjust background darkness")
                                        Slider(
                                            value = profile.wallpaperDim,
                                            onValueChange = { onUpdateProfile(profile.copy(wallpaperDim = it)) },
                                            valueRange = 0.0f..0.95f
                                        )
                                    }
                                    item {
                                        SectionHeading("Icon Style", "Banner, Square icon, or Circular icon")
                                        ChoiceRow(
                                            options = IconStyle.values().map { it.name },
                                            selectedIndex = IconStyle.values().indexOf(profile.iconStyle),
                                            accentColor = accentColor,
                                            onSelect = { onUpdateProfile(profile.copy(iconStyle = IconStyle.values()[it])) }
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SectionHeading("Font Choice", "Typography across the interface")
                                        ChoiceRow(
                                            options = FontFamilyChoice.values().map { it.name },
                                            selectedIndex = FontFamilyChoice.values().indexOf(profile.fontFamily),
                                            accentColor = accentColor,
                                            onSelect = { onUpdateProfile(profile.copy(fontFamily = FontFamilyChoice.values()[it])) }
                                        )
                                    }
                                    item {
                                        ToggleRow("Show App Labels", profile.showAppLabels, accentColor) {
                                            onUpdateProfile(profile.copy(showAppLabels = it))
                                        }
                                    }
                                }
                                4 -> { // Top Bar & Widgets
                                    item {
                                        SectionHeading("Top Bar Position", "Top, Bottom, or Hidden")
                                        ChoiceRow(
                                            options = TopBarPosition.values().map { it.name },
                                            selectedIndex = TopBarPosition.values().indexOf(profile.topBarPosition),
                                            accentColor = accentColor,
                                            onSelect = { onUpdateProfile(profile.copy(topBarPosition = TopBarPosition.values()[it])) }
                                        )
                                    }
                                    item {
                                        ToggleRow("24-Hour Clock", profile.clockFormat24h, accentColor) {
                                            onUpdateProfile(profile.copy(clockFormat24h = it))
                                        }
                                        ToggleRow("Show Seconds", profile.showSeconds, accentColor) {
                                            onUpdateProfile(profile.copy(showSeconds = it))
                                        }
                                        ToggleRow("Show Weather Widget", profile.showWeather, accentColor) {
                                            onUpdateProfile(profile.copy(showWeather = it))
                                        }
                                        ToggleRow("Wi-Fi Indicator", profile.showWifiIndicator, accentColor) {
                                            onUpdateProfile(profile.copy(showWifiIndicator = it))
                                        }
                                        ToggleRow("Bluetooth Indicator", profile.showBluetoothIndicator, accentColor) {
                                            onUpdateProfile(profile.copy(showBluetoothIndicator = it))
                                        }
                                        ToggleRow("USB Device Indicator", profile.showUsbIndicator, accentColor) {
                                            onUpdateProfile(profile.copy(showUsbIndicator = it))
                                        }
                                        ToggleRow("Storage Indicator", profile.showStorageIndicator, accentColor) {
                                            onUpdateProfile(profile.copy(showStorageIndicator = it))
                                        }
                                    }
                                }
                                5 -> { // Focus & Motion
                                    item {
                                        SectionHeading("Focus Effect", "Scale, Glow, Border, Parallax Tilt, or None")
                                        ChoiceRow(
                                            options = FocusEffect.values().map { it.name },
                                            selectedIndex = FocusEffect.values().indexOf(profile.focusEffect),
                                            accentColor = accentColor,
                                            onSelect = { onUpdateProfile(profile.copy(focusEffect = FocusEffect.values()[it])) }
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SectionHeading("Animation Speed", "Off, Fast, Normal, or Slow")
                                        ChoiceRow(
                                            options = AnimationSpeed.values().map { it.name },
                                            selectedIndex = AnimationSpeed.values().indexOf(profile.animationSpeed),
                                            accentColor = accentColor,
                                            onSelect = { onUpdateProfile(profile.copy(animationSpeed = AnimationSpeed.values()[it])) }
                                        )
                                    }
                                    item {
                                        ToggleRow("Navigation Sound Effects", profile.soundEffectsEnabled, accentColor) {
                                            onUpdateProfile(profile.copy(soundEffectsEnabled = it))
                                        }
                                    }
                                }
                                6 -> { // Behavior & Remote
                                    item {
                                        SectionHeading("Auto-Launch on Boot", "Automatically open an app when TV turns on")
                                        SettingClickableRow(
                                            title = "Auto-Launch App",
                                            subtitle = profile.autoLaunchBootPackage ?: "Disabled (Default to Launcher)",
                                            accentColor = accentColor,
                                            onClick = {
                                                // Cycle or toggle
                                                onUpdateProfile(profile.copy(autoLaunchBootPackage = null))
                                            }
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SectionHeading("Long-Press Home Action", "Remote button custom behavior")
                                        ChoiceRow(
                                            options = RemoteShortcutAction.values().map { it.name },
                                            selectedIndex = RemoteShortcutAction.values().indexOf(profile.longPressHomeAction),
                                            accentColor = accentColor,
                                            onSelect = { onUpdateProfile(profile.copy(longPressHomeAction = RemoteShortcutAction.values()[it])) }
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SectionHeading("Security & PIN", "Protect settings behind a 4-digit PIN")
                                        ToggleRow("Lock Settings with PIN", profile.pinProtectedSettings, accentColor) {
                                            onUpdateProfile(profile.copy(pinProtectedSettings = it, settingsPin = if (it && profile.settingsPin.isEmpty()) "0000" else profile.settingsPin))
                                        }
                                    }
                                }
                                7 -> { // Hidden Apps
                                    item {
                                        SectionHeading("Hidden Apps Manager", "Click any app to toggle hidden visibility")
                                    }
                                    items(allApps) { app ->
                                        HiddenAppRow(app = app, accentColor = accentColor, onToggle = { onToggleHideApp(app) })
                                    }
                                }
                                8 -> { // Backup & Reset
                                    item {
                                        SectionHeading("DataStore Backup & Restore", "Export all configurations as JSON")
                                        SettingClickableRow(
                                            title = "Export Settings to JSON",
                                            subtitle = "Save current profile to device storage",
                                            accentColor = accentColor,
                                            onClick = onExportBackup
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SettingClickableRow(
                                            title = "Import Settings from JSON",
                                            subtitle = "Restore custom configuration file",
                                            accentColor = accentColor,
                                            onClick = onImportBackup
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        SettingClickableRow(
                                            title = "Reset All Settings to Default",
                                            subtitle = "Revert to clean out-of-the-box launcher state",
                                            accentColor = Color(0xFFF87171),
                                            onClick = onResetDefaults
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        SettingClickableRow(
                                            title = "Switch Back to Original TV Launcher",
                                            subtitle = "Launch stock Google TV / Android TV home or open Home App chooser",
                                            accentColor = Color(0xFFFBBF24),
                                            onClick = onSwitchToOriginalLauncher
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Right Live Preview Card
                    Column(
                        modifier = Modifier
                            .width(260.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF0D0F16))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "LIVE PREVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA1A1AA)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        val previewApp = allApps.firstOrNull() ?: AppItem(
                            packageName = "preview.app",
                            activityName = "MainActivity",
                            label = "Aura Player",
                            dominantColor = 0xFF4F46E5.toInt()
                        )
                        AppCard(
                            app = previewApp,
                            onClick = {},
                            onLongClick = {},
                            accentColor = accentColor,
                            cornerRadiusStyle = profile.cornerRadiusStyle,
                            cardSize = profile.cardSize,
                            iconStyle = profile.iconStyle,
                            focusEffect = profile.focusEffect,
                            animationSpeed = profile.animationSpeed,
                            soundEffectsEnabled = false,
                            fontFamilyChoice = profile.fontFamily,
                            textSizeScale = profile.textSizeScale,
                            showLabel = profile.showAppLabels,
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 6.dp)) {
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = subtitle, fontSize = 12.sp, color = Color(0xFFA1A1AA))
    }
}

@Composable
private fun ChoiceRow(
    options: List<String>,
    selectedIndex: Int,
    accentColor: Color,
    onSelect: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        items(options.size) { index ->
            val isSelected = index == selectedIndex
            val interactionSource = remember { MutableInteractionSource() }
            val isFocused by interactionSource.collectIsFocusedAsState()

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when {
                            isFocused -> accentColor
                            isSelected -> accentColor.copy(alpha = 0.35f)
                            else -> Color(0xFF27272A)
                        }
                    )
                    .border(
                        width = if (isFocused || isSelected) 2.dp else 0.dp,
                        color = if (isFocused) Color.White else (if (isSelected) accentColor else Color.Transparent),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .focusable(interactionSource = interactionSource)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onSelect(index) }
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = options[index].replace('_', ' ').lowercase().capitalizeFirst(),
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    checked: Boolean,
    accentColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) accentColor.copy(alpha = 0.15f) else Color.Transparent)
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 13.sp, color = Color.White)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingClickableRow(
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) accentColor.copy(alpha = 0.25f) else Color(0xFF1E2028))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) accentColor else Color(0xFF272A34),
                shape = RoundedCornerShape(8.dp)
            )
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        Text(text = subtitle, fontSize = 12.sp, color = Color(0xFFA1A1AA))
    }
}

@Composable
private fun TabNavItem(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {
                    isFocused -> accentColor
                    isSelected -> accentColor.copy(alpha = 0.35f)
                    else -> Color.Transparent
                }
            )
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) Color.White else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
private fun SettingsSmallButton(
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) accentColor else Color(0xFF27272A))
            .focusable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun HiddenAppRow(
    app: AppItem,
    accentColor: Color,
    onToggle: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) accentColor.copy(alpha = 0.2f) else Color(0xFF1E2028))
            .focusable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = app.displayLabel, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(text = app.packageName, color = Color(0xFFA1A1AA), fontSize = 11.sp)
        }
        Icon(
            imageVector = if (app.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = null,
            tint = if (app.isHidden) Color(0xFFF87171) else Color(0xFF34D399),
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun String.capitalizeFirst(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun parseHexColor(hex: String): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = clean.toLong(16).toInt()
        if (clean.length == 6) {
            Color(colorInt or 0xFF000000.toInt())
        } else {
            Color(colorInt)
        }
    } catch (e: Exception) {
        Color(0xFF6366F1)
    }
}
