# Aura TV Launcher

A minimal, high-performance, ad-free Android TV / Google TV launcher built natively with **Kotlin**, **Jetpack Compose for TV**, and **Room Database**.

---

## 🚀 Why Native Jetpack Compose for TV over Flutter?

For a custom Android TV / Google TV launcher where **cold-start latency under 1 second**, **60fps D-pad remote responsiveness**, and **low memory consumption** are critical:
1. **Cold-Start Speed**: Native Kotlin does not incur the Flutter engine/Dart VM bootstrap overhead (~300–600ms on TV chipsets like Amlogic/MediaTek).
2. **Lean Focus Engine**: TV D-pad navigation uses Android's native focus system directly, eliminating focus desyncs common in cross-platform renderers.
3. **APK Size**: Native builds with R8 shrinking produce an APK under 6-8 MB vs 18-25+ MB for Flutter engines.
4. **Leanback APIs**: Native access to `PackageManager.getLeanbackLaunchIntentForPackage()`, `ResolveInfo.loadBanner()`, and live broadcast filters.

---

## 📁 Project Architecture

```
launcher/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/auratv/launcher/
│       │   ├── AuraTVApplication.kt       # Application class, async warm-up
│       │   ├── MainActivity.kt            # Launch activity, back/intent handlers
│       │   ├── MainViewModel.kt           # State orchestration & flows
│       │   ├── data/
│       │   │   ├── AppDao.kt              # Room DAO for persistence & sorting
│       │   │   ├── AppRepository.kt       # Intent queries & background sync
│       │   │   ├── IconDiskCache.kt       # Off-thread bitmap disk caching & Palette
│       │   │   ├── LauncherDatabase.kt    # SQLite Room DB definition
│       │   │   ├── PackageChangeReceiver.kt# Broadcast receiver (install/uninstall)
│       │   │   └── SettingsRepository.kt  # User preferences (columns, wallpaper)
│       │   ├── model/
│       │   │   └── AppItem.kt             # Data models & backup schemas
│       │   └── ui/
│       │       ├── AppActionDialog.kt     # Long-press popup menu (Pin/Hide/Uninstall)
│       │       ├── AppCard.kt             # Remote focusable card (1.1x scale, glow)
│       │       ├── HomeScreen.kt          # Favorites, Recents, and Grid
│       │       ├── SettingsDialog.kt      # Grid size, hidden apps, wallpaper, backup
│       │       └── TopBar.kt              # Clock, date, Wi-Fi status, quick settings
│       └── res/
│           ├── drawable/                  # Vector banners & foreground icons
│           └── values/                    # Strings and fullscreen TV styles
├── gradle/
│   ├── libs.versions.toml                 # Version catalog
│   └── wrapper/gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
└── build_apk.sh
```

---

## 🛠️ Building the APK

### Prerequisites
- JDK 17+
- Android SDK (API 35 compileSdk, minSdk 26)

### Option 1: Android Studio
Open the project directory in Android Studio (Ladybug / Koala or newer), sync Gradle, and select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

### Option 2: Terminal
```bash
./gradlew assembleDebug
# Or for optimized release:
./gradlew assembleRelease
```
The output APK will be at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📺 ADB Installation & Setting Default Launcher

### 1. Connect to your Android TV via ADB
Enable Developer Options and USB/Network Debugging on your TV, then run:
```bash
# Connect over Wi-Fi (replace with your TV's IP address):
adb connect 192.168.1.50:5555

# Verify device is connected:
adb devices
```

### 2. Install the APK
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 3. Set Aura TV Launcher as Default
When you press the **Home** button on your TV remote, Android will display a prompt:
- Choose **"Aura TV Launcher"** -> select **"Always"**.

If the stock launcher does not show the prompt or overrides the Home key:
```bash
# Disable the stock Google TV launcher:
adb shell pm disable-user --user 0 com.google.android.apps.tv.launcherx

# Or for standard Android TV leanback launcher:
adb shell pm disable-user --user 0 com.google.android.leanbacklauncher
```

---

## 🔄 How to Revert to the Stock Launcher

To re-enable and restore the official stock launcher at any time:

### For Google TV:
```bash
adb shell pm enable com.google.android.apps.tv.launcherx
```

### For Android TV (Leanback):
```bash
adb shell pm enable com.google.android.leanbacklauncher
```

Then press the Home button on your remote.

