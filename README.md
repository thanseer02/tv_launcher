# LeanTV - Native Android TV Launcher

LeanTV is an ultra-lightweight, native Android TV launcher built specifically for **Xiaomi / Mi TV** and Android TV devices running Android 12 with 2 GB RAM.

## Key Features & TV Optimizations
- **Ultra-low Memory Usage**: Target memory footprint under 30-40 MB PSS. Zero background network services, zero Firebase/Analytics, no heavy WebView, and no Room/SQLite overhead.
- **TV Remote (D-Pad) Optimized**: High-visibility focus indicators, smooth 1.08x scale card animation, and full directional navigation.
- **Real App Discovery**: Fast, non-blocking asynchronous `PackageManager` queries supporting both TV Leanback apps (`CATEGORY_LEANBACK_LAUNCHER`) and standard sideloaded apps (`CATEGORY_LAUNCHER`).
- **Android 12 Package Visibility**: Full `<queries>` declarations in compliance with Android 11+ and 12 restrictions.
- **Context Actions (Menu Key / Long Press D-pad)**: Add/Remove Favorites, Hide Apps, App Info, and System Uninstall.
- **OLED Dark Home Screen**: Pure OLED Black theme by default to reduce backlight heat and power consumption on TVs, with Charcoal Dark and Deep Navy options.
- **Bounded LRU Memory Cache**: Strict 8 MB upper cap on icon/banner bitmap caching with instant cache eviction on low-memory triggers.

---

## Pre-built APKs Location

- **Optimized Release APK (R8 Minified & Resource Shrunk)**:
  `app/build/outputs/apk/release/app-release.apk`
- **Debug APK**:
  `app/build/outputs/apk/debug/app-debug.apk`

---

## Installation Instructions (Xiaomi / Mi TV & Android 12)

### 1. Enable USB/Network Debugging on Mi TV
1. On your Mi TV, navigate to **Settings** > **Device Preferences** > **About**.
2. Scroll to **Build** and click the remote **Center (OK)** button 7 times until you see the message: *"You are now a developer!"*.
3. Go back to **Settings** > **Device Preferences** > **Developer Options**.
4. Enable **USB Debugging** (and **Network Debugging / Wireless Debugging** if connecting over Wi-Fi).

### 2. Connect via ADB
From your computer on the same Wi-Fi network:
```bash
adb connect <TV_IP_ADDRESS>:5555
```
*(Accept the authorization prompt on the TV screen using your remote)*

### 3. Install LeanTV APK
Run from the project directory:
```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

### 4. Setting LeanTV as Default Home Launcher
- Press the **Home** button on your Mi remote. Android TV will prompt: *"Open with: Select a Home app"*.
- Select **LeanTV** and choose **Always**.

Alternatively, open **Settings** inside LeanTV and select **Set Default Launcher**, or trigger via ADB:
```bash
adb shell am start -a android.settings.HOME_SETTINGS
```

*(Note: In compliance with safe Android development practices, Google TV system packages are never silently modified or disabled).*

# tv_launcher
