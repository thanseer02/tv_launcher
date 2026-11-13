package com.auratv.launcher.ui

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.auratv.launcher.model.FontFamilyChoice
import com.auratv.launcher.model.LauncherProfile
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TopBar(
    profile: LauncherProfile,
    accentColor: Color,
    onOpenSettings: () -> Unit,
    onSwitchToOriginalLauncher: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Live clock and date state
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(profile.clockFormat24h, profile.showSeconds, profile.dateFormatPattern) {
        val pattern = when {
            profile.clockFormat24h && profile.showSeconds -> "HH:mm:ss"
            profile.clockFormat24h -> "HH:mm"
            profile.showSeconds -> "h:mm:ss a"
            else -> "h:mm a"
        }
        val timeFormat = SimpleDateFormat(pattern, Locale.getDefault())
        val dateFormat = SimpleDateFormat(profile.dateFormatPattern, Locale.getDefault())
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)
            delay(1000L)
        }
    }

    // Network connectivity listener
    var isConnectedToWifi by remember { mutableStateOf(false) }
    DisposableEffect(context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val caps = connectivityManager.getNetworkCapabilities(network)
                isConnectedToWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                        caps?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
            }

            override fun onLost(network: Network) {
                isConnectedToWifi = false
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                isConnectedToWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)

        val active = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(active)
        isConnectedToWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                caps?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    // Bluetooth status
    var isBluetoothEnabled by remember {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        mutableStateOf(btAdapter?.isEnabled == true)
    }

    // USB / Storage detection
    var usbConnected by remember { mutableStateOf(false) }
    var freeStoragePercent by remember { mutableStateOf(0) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent?.action == "android.hardware.usb.action.USB_STATE") {
                    usbConnected = intent.extras?.getBoolean("connected") ?: false
                }
            }
        }
        context.registerReceiver(receiver, IntentFilter("android.hardware.usb.action.USB_STATE"))

        try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val available = stat.availableBlocksLong * stat.blockSizeLong
            val total = stat.blockCountLong * stat.blockSizeLong
            freeStoragePercent = if (total > 0) ((available * 100) / total).toInt() else 0
        } catch (e: Exception) {
            freeStoragePercent = 50
        }

        onDispose {
            try { context.unregisterReceiver(receiver) } catch (e: Exception) {}
        }
    }

    val font = when (profile.fontFamily) {
        FontFamilyChoice.DEFAULT -> FontFamily.Default
        FontFamilyChoice.SANS_SERIF -> FontFamily.SansSerif
        FontFamilyChoice.SERIF -> FontFamily.Serif
        FontFamilyChoice.MONOSPACE -> FontFamily.Monospace
        FontFamilyChoice.ROUNDED -> FontFamily.Default
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Free My TV style Title & Tabs
        Column {
            Text(
                text = "For You",
                fontSize = (38f * profile.textSizeScale).sp,
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Explore",
                    fontSize = (15f * profile.textSizeScale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Movies",
                    fontSize = (15f * profile.textSizeScale).sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF8E8E93)
                )
                Text(
                    text = "Shows",
                    fontSize = (15f * profile.textSizeScale).sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF8E8E93)
                )
                Text(
                    text = "•   $currentTime",
                    fontSize = (14f * profile.textSizeScale).sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFA1A1AA)
                )
            }
        }

        // Status Indicators & Quick Settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (profile.showStorageIndicator) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SdStorage,
                        contentDescription = "Storage",
                        tint = Color(0xFFA1A1AA),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$freeStoragePercent% free",
                        fontSize = 11.sp,
                        color = Color(0xFFA1A1AA)
                    )
                }
            }

            if (profile.showUsbIndicator && usbConnected) {
                Icon(
                    imageVector = Icons.Default.Usb,
                    contentDescription = "USB Connected",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (profile.showBluetoothIndicator) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = "Bluetooth",
                    tint = if (isBluetoothEnabled) Color(0xFF60A5FA) else Color(0xFF52525B),
                    modifier = Modifier.size(18.dp)
                )
            }

            if (profile.showWifiIndicator) {
                TopBarIconButton(
                    icon = if (isConnectedToWifi) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = "Wi-Fi Settings",
                    tint = if (isConnectedToWifi) Color(0xFF34D399) else Color(0xFFF87171),
                    accentColor = accentColor,
                    onClick = {
                        try {
                            context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            })
                        } catch (e: Exception) {
                            context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            })
                        }
                    }
                )
            }

            // Display settings
            TopBarIconButton(
                icon = Icons.Default.DisplaySettings,
                contentDescription = "Display Settings",
                tint = Color(0xFF93C5FD),
                accentColor = accentColor,
                onClick = {
                    try {
                        context.startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    } catch (e: Exception) {
                        context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    }
                }
            )

            // System settings
            TopBarIconButton(
                icon = Icons.Default.Settings,
                contentDescription = "System Settings",
                tint = Color(0xFFE2E8F0),
                accentColor = accentColor,
                onClick = {
                    try {
                        context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            )

            // Switch Back to Original TV Launcher
            TopBarIconButton(
                icon = Icons.Default.Home,
                contentDescription = "Switch to Original TV Launcher",
                tint = Color(0xFFFBBF24),
                accentColor = accentColor,
                onClick = onSwitchToOriginalLauncher
            )

            // Aura TV Settings Panel
            TopBarIconButton(
                icon = Icons.Default.Settings,
                contentDescription = "Launcher Settings",
                tint = accentColor,
                accentColor = accentColor,
                onClick = onOpenSettings
            )
        }
    }
}

@Composable
fun TopBarIconButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.2f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "topBarScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .size(38.dp)
            .clip(CircleShape)
            .background(if (isFocused) accentColor.copy(alpha = 0.25f) else Color(0x1AFFFFFF))
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) accentColor else Color.Transparent,
                shape = CircleShape
            )
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(19.dp)
        )
    }
}
