#!/bin/sh

# Setup standard Android SDK location if not set
if [ -z "$ANDROID_HOME" ]; then
    if [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
    elif [ -d "$HOME/Android/Sdk" ]; then
        export ANDROID_HOME="$HOME/Android/Sdk"
    fi
fi

echo "==> Building Aura TV Launcher APK..."
chmod +x ./gradlew 2>/dev/null || true

# If gradlew wrapper jar is not present, use gradle from system or brew
if [ -f "./gradlew" ]; then
    ./gradlew assembleDebug
else
    gradle assembleDebug
fi

