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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.auratv.launcher.model.AppItem

@Composable
fun AppActionDialog(
    app: AppItem?,
    accentColor: Color,
    onDismiss: () -> Unit,
    onTogglePin: (AppItem) -> Unit,
    onHide: (AppItem) -> Unit,
    onUninstall: (AppItem) -> Unit,
    onAppInfo: (AppItem) -> Unit,
    onRename: (AppItem, String) -> Unit,
    onPickCustomIcon: (AppItem) -> Unit
) {
    AnimatedVisibility(
        visible = app != null,
        enter = fadeIn() + scaleIn(initialScale = 0.95f),
        exit = fadeOut() + scaleOut(targetScale = 0.95f),
        modifier = Modifier.zIndex(50f)
    ) {
        if (app == null) return@AnimatedVisibility

        var isRenaming by remember { mutableStateOf(false) }
        var currentLabelText by remember { mutableStateOf(app.displayLabel) }
        val firstItemFocusRequester = remember { FocusRequester() }

        LaunchedEffect(app) {
            firstItemFocusRequester.requestFocus()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xBB05050A))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(420.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF18181B))
                    .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(16.dp))
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = app.displayLabel,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = app.packageName,
                    fontSize = 12.sp,
                    color = Color(0xFFA1A1AA)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isRenaming) {
                    OutlinedTextField(
                        value = currentLabelText,
                        onValueChange = { currentLabelText = it },
                        label = { Text("App Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ActionRow(
                            icon = Icons.Default.Edit,
                            label = "Save",
                            accentColor = accentColor,
                            onClick = {
                                onRename(app, currentLabelText)
                                isRenaming = false
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ActionRow(
                            icon = Icons.Default.Info,
                            label = "Cancel",
                            accentColor = accentColor,
                            onClick = { isRenaming = false },
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    ActionRow(
                        icon = Icons.Default.PushPin,
                        label = if (app.isFavorite) "Unpin from Favorites" else "Pin to Favorites",
                        accentColor = accentColor,
                        focusRequester = firstItemFocusRequester,
                        onClick = {
                            onTogglePin(app)
                            onDismiss()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ActionRow(
                        icon = Icons.Default.Edit,
                        label = "Rename App",
                        accentColor = accentColor,
                        onClick = { isRenaming = true }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ActionRow(
                        icon = Icons.Default.Image,
                        label = "Custom App Icon",
                        accentColor = accentColor,
                        onClick = {
                            onPickCustomIcon(app)
                            onDismiss()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ActionRow(
                        icon = Icons.Default.VisibilityOff,
                        label = if (app.isHidden) "Unhide App" else "Hide App",
                        accentColor = accentColor,
                        onClick = {
                            onHide(app)
                            onDismiss()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ActionRow(
                        icon = Icons.Default.Info,
                        label = "App Info",
                        accentColor = accentColor,
                        onClick = {
                            onAppInfo(app)
                            onDismiss()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ActionRow(
                        icon = Icons.Default.Delete,
                        label = "Uninstall",
                        accentColor = accentColor,
                        textColor = Color(0xFFF87171),
                        onClick = {
                            onUninstall(app)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    label: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    focusRequester: FocusRequester? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) accentColor.copy(alpha = 0.25f) else Color(0xFF27272A))
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) accentColor else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isFocused) accentColor else textColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}
