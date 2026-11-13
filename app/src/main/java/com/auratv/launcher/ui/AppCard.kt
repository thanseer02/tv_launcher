package com.auratv.launcher.ui

import android.view.SoundEffectConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.auratv.launcher.model.AnimationSpeed
import com.auratv.launcher.model.AppItem
import com.auratv.launcher.model.CardSize
import com.auratv.launcher.model.CornerRadiusStyle
import com.auratv.launcher.model.FocusEffect
import com.auratv.launcher.model.FontFamilyChoice
import com.auratv.launcher.model.IconStyle
import java.io.File

@Composable
fun AppCard(
    app: AppItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    accentColor: Color,
    cornerRadiusStyle: CornerRadiusStyle,
    cardSize: CardSize,
    iconStyle: IconStyle,
    focusEffect: FocusEffect,
    animationSpeed: AnimationSpeed,
    soundEffectsEnabled: Boolean,
    fontFamilyChoice: FontFamilyChoice,
    textSizeScale: Float,
    showLabel: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Sound effect trigger on navigation focus change
    LaunchedEffect(isFocused) {
        if (isFocused && soundEffectsEnabled) {
            view.playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT)
        }
    }

    val animDuration = when (animationSpeed) {
        AnimationSpeed.OFF -> 0
        AnimationSpeed.FAST -> 90
        AnimationSpeed.NORMAL -> 150
        AnimationSpeed.SLOW -> 300
    }

    // Scale animation
    val targetScale = if (isFocused && (focusEffect == FocusEffect.SCALE || focusEffect == FocusEffect.PARALLAX)) {
        when (cardSize) {
            CardSize.SMALL -> 1.15f
            CardSize.MEDIUM -> 1.10f
            CardSize.LARGE -> 1.07f
        }
    } else 1.0f

    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = animDuration),
        label = "cardScale"
    )

    // Parallax tilt effect
    val rotationY by animateFloatAsState(
        targetValue = if (isFocused && focusEffect == FocusEffect.PARALLAX) 4f else 0f,
        animationSpec = tween(durationMillis = animDuration),
        label = "cardTilt"
    )

    val shape = when (cornerRadiusStyle) {
        CornerRadiusStyle.SHARP -> RoundedCornerShape(2.dp)
        CornerRadiusStyle.ROUNDED -> RoundedCornerShape(14.dp)
        CornerRadiusStyle.PILL -> RoundedCornerShape(26.dp)
    }

    val cardBgColor = Color(app.dominantColor)
    val font = when (fontFamilyChoice) {
        FontFamilyChoice.DEFAULT -> FontFamily.Default
        FontFamilyChoice.SANS_SERIF -> FontFamily.SansSerif
        FontFamilyChoice.SERIF -> FontFamily.Serif
        FontFamilyChoice.MONOSPACE -> FontFamily.Monospace
        FontFamilyChoice.ROUNDED -> FontFamily.Default
    }

    val showBanner = iconStyle == IconStyle.BANNER && !app.bannerPath.isNullOrEmpty()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                if (focusEffect == FocusEffect.PARALLAX) {
                    this.rotationY = rotationY
                    cameraDistance = 12f * density
                }
            }
            .zIndex(if (isFocused) 10f else 1f)
            .clip(shape)
            .border(
                border = BorderStroke(
                    width = if (isFocused && focusEffect != FocusEffect.NONE) 2.5.dp else 1.dp,
                    color = if (isFocused && focusEffect != FocusEffect.NONE) accentColor else Color(0x22FFFFFF)
                ),
                shape = shape
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        cardBgColor.copy(alpha = 0.85f),
                        cardBgColor.copy(alpha = 0.45f)
                    )
                )
            )
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.action == android.view.KeyEvent.ACTION_DOWN) {
                    if (keyEvent.nativeKeyEvent.isLongPress &&
                        (keyEvent.key == Key.DirectionCenter || keyEvent.key == Key.Enter)
                    ) {
                        onLongClick()
                        return@onPreviewKeyEvent true
                    }
                }
                false
            }
            .padding(if (showBanner) 0.dp else 8.dp)
    ) {
        val effectiveIcon = app.effectiveIconPath

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(if (showBanner) 16f / 9f else 1f),
            contentAlignment = Alignment.Center
        ) {
            if (showBanner) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(File(app.bannerPath!!))
                        .crossfade(true)
                        .build(),
                    contentDescription = app.displayLabel,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val iconShapeModifier = when (iconStyle) {
                    IconStyle.CIRCLE -> Modifier.clip(CircleShape)
                    IconStyle.SQUARE -> Modifier.clip(RoundedCornerShape(8.dp))
                    IconStyle.BANNER -> Modifier
                }
                val iconSizeDp = when (cardSize) {
                    CardSize.SMALL -> 46.dp
                    CardSize.MEDIUM -> 58.dp
                    CardSize.LARGE -> 72.dp
                }

                if (!effectiveIcon.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(File(effectiveIcon))
                            .crossfade(true)
                            .build(),
                        contentDescription = app.displayLabel,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(iconSizeDp)
                            .then(iconShapeModifier)
                    )
                }
            }
        }

        if (showLabel && (!showBanner || iconStyle != IconStyle.BANNER)) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.displayLabel,
                color = if (isFocused) Color.White else Color(0xFFD1D5DB),
                fontSize = (13f * textSizeScale).sp,
                fontFamily = font,
                fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}
