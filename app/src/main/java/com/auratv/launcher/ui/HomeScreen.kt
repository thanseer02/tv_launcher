package com.auratv.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.auratv.launcher.model.AnimationSpeed
import com.auratv.launcher.model.AppItem
import com.auratv.launcher.model.CardSize
import com.auratv.launcher.model.CornerRadiusStyle
import com.auratv.launcher.model.FocusEffect
import com.auratv.launcher.model.FontFamilyChoice
import com.auratv.launcher.model.IconStyle
import com.auratv.launcher.model.LayoutStyle
import com.auratv.launcher.model.LauncherProfile
import com.auratv.launcher.model.ThemeMode
import com.auratv.launcher.model.TopBarPosition
import com.auratv.launcher.model.WallpaperType
import kotlinx.coroutines.delay
import java.io.File
import java.util.Calendar

@Composable
fun HomeScreen(
    profile: LauncherProfile,
    favorites: List<AppItem>,
    recentApps: List<AppItem>,
    allApps: List<AppItem>,
    accentColor: Color,
    isReorderingFavorites: Boolean,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onMoveFavoriteLeft: (AppItem) -> Unit,
    onMoveFavoriteRight: (AppItem) -> Unit,
    onFinishReordering: () -> Unit,
    onOpenSettings: () -> Unit,
    onSwitchToOriginalLauncher: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Resolve background base color based on ThemeMode
    val isAutoDay = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        hour in 7..18
    }

    val backgroundColor = when (profile.themeMode) {
        ThemeMode.DARK -> Color(0xFF0A0C14)
        ThemeMode.AMOLED -> Color(0xFF000000)
        ThemeMode.LIGHT -> Color(0xFFF3F4F6)
        ThemeMode.AUTO -> if (isAutoDay) Color(0xFFF3F4F6) else Color(0xFF0A0C14)
    }

    // Slideshow state
    var slideshowImageIndex by remember { mutableStateOf(0) }
    val slideshowFiles = remember(profile.wallpaperFolderPath) {
        if (!profile.wallpaperFolderPath.isNullOrEmpty()) {
            val folder = File(profile.wallpaperFolderPath)
            if (folder.exists() && folder.isDirectory) {
                folder.listFiles { f -> f.extension.lowercase() in listOf("jpg", "jpeg", "png", "webp") }?.toList() ?: emptyList()
            } else emptyList()
        } else emptyList()
    }

    LaunchedEffect(slideshowFiles.size) {
        if (slideshowFiles.size > 1) {
            while (true) {
                delay(15000L) // change wallpaper every 15 seconds
                slideshowImageIndex = (slideshowImageIndex + 1) % slideshowFiles.size
            }
        }
    }

    val currentWallpaperFile = when (profile.wallpaperType) {
        WallpaperType.IMAGE -> profile.wallpaperPath?.let { File(it) }
        WallpaperType.SLIDESHOW -> slideshowFiles.getOrNull(slideshowImageIndex)
        else -> null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Wallpaper rendering
        if (currentWallpaperFile != null && currentWallpaperFile.exists()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentWallpaperFile)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Overlay dimming
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = profile.wallpaperDim))
            )
        } else if (profile.wallpaperType == WallpaperType.GRADIENT) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.25f),
                                Color(0xFF05060A)
                            )
                        )
                    )
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            if (profile.showTopBar && profile.topBarPosition == TopBarPosition.TOP) {
                TopBar(
                    profile = profile,
                    accentColor = accentColor,
                    onOpenSettings = onOpenSettings,
                    onSwitchToOriginalLauncher = onSwitchToOriginalLauncher
                )
            }

            // Main Content Area
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                // Free My TV style Hero Featured Spotlight
                if (allApps.isNotEmpty()) {
                    item(key = "section_hero_spotlight") {
                        HeroFeaturedSection(
                            apps = allApps,
                            accentColor = accentColor,
                            onAppClick = onAppClick
                        )
                    }
                }

                // Section Order Rendering
                for (sectionKey in profile.sectionOrder) {
                    when (sectionKey) {
                        "favorites" -> {
                            if (profile.showFavorites && favorites.isNotEmpty()) {
                                item(key = "section_favorites") {
                                    FavoritesSection(
                                        favorites = favorites,
                                        accentColor = accentColor,
                                        profile = profile,
                                        isReordering = isReorderingFavorites,
                                        onAppClick = onAppClick,
                                        onAppLongClick = onAppLongClick,
                                        onMoveLeft = onMoveFavoriteLeft,
                                        onMoveRight = onMoveFavoriteRight,
                                        onFinishReordering = onFinishReordering
                                    )
                                }
                            }
                        }
                        "recents" -> {
                            if (profile.showRecents && recentApps.isNotEmpty()) {
                                item(key = "section_recents") {
                                    RecentsSection(
                                        recentApps = recentApps,
                                        accentColor = accentColor,
                                        profile = profile,
                                        onAppClick = onAppClick,
                                        onAppLongClick = onAppLongClick
                                    )
                                }
                            }
                        }
                        "categories" -> {
                            if (profile.layoutStyle == LayoutStyle.CATEGORIES) {
                                val categories = allApps.groupBy { it.category }
                                categories.forEach { (catName, catApps) ->
                                    item(key = "category_$catName") {
                                        CategoryRowSection(
                                            title = catName,
                                            apps = catApps,
                                            accentColor = accentColor,
                                            profile = profile,
                                            onAppClick = onAppClick,
                                            onAppLongClick = onAppLongClick
                                        )
                                    }
                                }
                            }
                        }
                        "all_apps" -> {
                            if (profile.showAllApps && profile.layoutStyle != LayoutStyle.CATEGORIES) {
                                item(key = "section_all_apps") {
                                    AllAppsCatalogSection(
                                        apps = allApps,
                                        accentColor = accentColor,
                                        profile = profile,
                                        onAppClick = onAppClick,
                                        onAppLongClick = onAppLongClick
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom TopBar if positioned at Bottom
            if (profile.showTopBar && profile.topBarPosition == TopBarPosition.BOTTOM) {
                TopBar(
                    profile = profile,
                    accentColor = accentColor,
                    onOpenSettings = onOpenSettings,
                    onSwitchToOriginalLauncher = onSwitchToOriginalLauncher
                )
            }
        }
    }
}

@Composable
private fun HeroFeaturedSection(
    apps: List<AppItem>,
    accentColor: Color,
    onAppClick: (AppItem) -> Unit
) {
    val featuredApp = apps.firstOrNull() ?: return
    val secondaryApp = apps.getOrNull(1)
    val tertiaryApp = apps.getOrNull(2)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Main wide hero card (3 columns equivalent in Free My TV)
            Box(
                modifier = Modifier
                    .weight(1.8f)
                    .height(260.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(featuredApp.dominantColor).copy(alpha = 0.9f),
                                Color(0xFF161922)
                            )
                        )
                    )
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(22.dp))
                    .clickable { onAppClick(featuredApp) }
                    .padding(24.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    Text(
                        text = featuredApp.displayLabel,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Featured • Ready to play",
                        fontSize = 14.sp,
                        color = Color(0xFFA1A1AA)
                    )
                }
            }

            // Secondary card
            if (secondaryApp != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(260.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(secondaryApp.dominantColor).copy(alpha = 0.8f),
                                    Color(0xFF161922)
                                )
                            )
                        )
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(22.dp))
                        .clickable { onAppClick(secondaryApp) }
                        .padding(20.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = secondaryApp.displayLabel,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Tertiary card
            if (tertiaryApp != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(260.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(tertiaryApp.dominantColor).copy(alpha = 0.8f),
                                    Color(0xFF161922)
                                )
                            )
                        )
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(22.dp))
                        .clickable { onAppClick(tertiaryApp) }
                        .padding(20.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = tertiaryApp.displayLabel,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesSection(
    favorites: List<AppItem>,
    accentColor: Color,
    profile: LauncherProfile,
    isReordering: Boolean,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onMoveLeft: (AppItem) -> Unit,
    onMoveRight: (AppItem) -> Unit,
    onFinishReordering: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isReordering) "Favorites (Reordering Mode)" else "Favorites",
                fontSize = (17f * profile.textSizeScale).sp,
                fontWeight = FontWeight.Bold,
                color = if (isReordering) accentColor else Color(0xFFF1F5F9)
            )
            if (isReordering) {
                ReorderDoneButton(accentColor = accentColor, onClick = onFinishReordering)
            }
        }

        val cardWidth = when (profile.cardSize) {
            CardSize.SMALL -> 150.dp
            CardSize.MEDIUM -> 180.dp
            CardSize.LARGE -> 220.dp
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(favorites, key = { "fav_${it.packageName}" }) { app ->
                if (isReordering) {
                    ReorderableAppCard(
                        app = app,
                        accentColor = accentColor,
                        onMoveLeft = { onMoveLeft(app) },
                        onMoveRight = { onMoveRight(app) }
                    )
                } else {
                    AppCard(
                        app = app,
                        onClick = { onAppClick(app) },
                        onLongClick = { onAppLongClick(app) },
                        accentColor = accentColor,
                        cornerRadiusStyle = profile.cornerRadiusStyle,
                        cardSize = profile.cardSize,
                        iconStyle = profile.iconStyle,
                        focusEffect = profile.focusEffect,
                        animationSpeed = profile.animationSpeed,
                        soundEffectsEnabled = profile.soundEffectsEnabled,
                        fontFamilyChoice = profile.fontFamily,
                        textSizeScale = profile.textSizeScale,
                        showLabel = profile.showAppLabels,
                        modifier = Modifier.width(cardWidth)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentsSection(
    recentApps: List<AppItem>,
    accentColor: Color,
    profile: LauncherProfile,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit
) {
    Column {
        Text(
            text = "Recently Used",
            fontSize = (17f * profile.textSizeScale).sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF1F5F9),
            modifier = Modifier.padding(horizontal = 48.dp, vertical = 6.dp)
        )

        val cardWidth = when (profile.cardSize) {
            CardSize.SMALL -> 140.dp
            CardSize.MEDIUM -> 160.dp
            CardSize.LARGE -> 200.dp
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(recentApps, key = { "recent_${it.packageName}" }) { app ->
                AppCard(
                    app = app,
                    onClick = { onAppClick(app) },
                    onLongClick = { onAppLongClick(app) },
                    accentColor = accentColor,
                    cornerRadiusStyle = profile.cornerRadiusStyle,
                    cardSize = profile.cardSize,
                    iconStyle = profile.iconStyle,
                    focusEffect = profile.focusEffect,
                    animationSpeed = profile.animationSpeed,
                    soundEffectsEnabled = profile.soundEffectsEnabled,
                    fontFamilyChoice = profile.fontFamily,
                    textSizeScale = profile.textSizeScale,
                    showLabel = profile.showAppLabels,
                    modifier = Modifier.width(cardWidth)
                )
            }
        }
    }
}

@Composable
private fun CategoryRowSection(
    title: String,
    apps: List<AppItem>,
    accentColor: Color,
    profile: LauncherProfile,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = (16f * profile.textSizeScale).sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF1F5F9),
            modifier = Modifier.padding(horizontal = 48.dp, vertical = 6.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(apps, key = { "cat_${title}_${it.packageName}" }) { app ->
                AppCard(
                    app = app,
                    onClick = { onAppClick(app) },
                    onLongClick = { onAppLongClick(app) },
                    accentColor = accentColor,
                    cornerRadiusStyle = profile.cornerRadiusStyle,
                    cardSize = profile.cardSize,
                    iconStyle = profile.iconStyle,
                    focusEffect = profile.focusEffect,
                    animationSpeed = profile.animationSpeed,
                    soundEffectsEnabled = profile.soundEffectsEnabled,
                    fontFamilyChoice = profile.fontFamily,
                    textSizeScale = profile.textSizeScale,
                    showLabel = profile.showAppLabels,
                    modifier = Modifier.width(170.dp)
                )
            }
        }
    }
}

@Composable
private fun AllAppsCatalogSection(
    apps: List<AppItem>,
    accentColor: Color,
    profile: LauncherProfile,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 48.dp)) {
        Text(
            text = "All Apps (${apps.size})",
            fontSize = (17f * profile.textSizeScale).sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF1F5F9),
            modifier = Modifier.padding(vertical = 6.dp)
        )

        when (profile.layoutStyle) {
            LayoutStyle.SINGLE_ROW -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(apps, key = { "single_${it.packageName}" }) { app ->
                        AppCard(
                            app = app,
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) },
                            accentColor = accentColor,
                            cornerRadiusStyle = profile.cornerRadiusStyle,
                            cardSize = profile.cardSize,
                            iconStyle = profile.iconStyle,
                            focusEffect = profile.focusEffect,
                            animationSpeed = profile.animationSpeed,
                            soundEffectsEnabled = profile.soundEffectsEnabled,
                            fontFamilyChoice = profile.fontFamily,
                            textSizeScale = profile.textSizeScale,
                            showLabel = profile.showAppLabels,
                            modifier = Modifier.width(170.dp)
                        )
                    }
                }
            }
            LayoutStyle.CLASSIC_LIST -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    apps.forEach { app ->
                        ClassicListRow(
                            app = app,
                            accentColor = accentColor,
                            profile = profile,
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) }
                        )
                    }
                }
            }
            else -> { // GRID
                val rows = (apps.size + profile.gridColumns - 1) / profile.gridColumns
                val estimatedHeight = (rows * 145).coerceAtLeast(145)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(profile.gridColumns),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    userScrollEnabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(estimatedHeight.dp)
                ) {
                    items(apps, key = { "grid_${it.packageName}" }) { app ->
                        AppCard(
                            app = app,
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) },
                            accentColor = accentColor,
                            cornerRadiusStyle = profile.cornerRadiusStyle,
                            cardSize = profile.cardSize,
                            iconStyle = profile.iconStyle,
                            focusEffect = profile.focusEffect,
                            animationSpeed = profile.animationSpeed,
                            soundEffectsEnabled = profile.soundEffectsEnabled,
                            fontFamilyChoice = profile.fontFamily,
                            textSizeScale = profile.textSizeScale,
                            showLabel = profile.showAppLabels
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClassicListRow(
    app: AppItem,
    accentColor: Color,
    profile: LauncherProfile,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) accentColor.copy(alpha = 0.25f) else Color(0xFF181A22))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) accentColor else Color(0x22FFFFFF),
                shape = RoundedCornerShape(8.dp)
            )
            .focusable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val effectiveIcon = app.effectiveIconPath
        if (!effectiveIcon.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(effectiveIcon))
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.displayLabel,
                fontSize = 15.sp,
                fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                color = Color.White
            )
            Text(text = app.packageName, fontSize = 11.sp, color = Color(0xFFA1A1AA))
        }
    }
}

@Composable
fun ReorderableAppCard(
    app: AppItem,
    accentColor: Color,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(accentColor.copy(alpha = 0.2f))
            .border(2.dp, accentColor, RoundedCornerShape(14.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = app.displayLabel,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ReorderArrowButton(icon = Icons.Default.ArrowBack, accentColor = accentColor, onClick = onMoveLeft)
            ReorderArrowButton(icon = Icons.Default.ArrowForward, accentColor = accentColor, onClick = onMoveRight)
        }
    }
}

@Composable
fun ReorderArrowButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun ReorderDoneButton(
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) accentColor else accentColor.copy(alpha = 0.5f))
            .focusable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Done Reordering", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
