#!/usr/bin/env bash
GRADLE_BIN="/Users/tanseer/.gradle/wrapper/dists/gradle-8.10.2-bin/a04bxjujx95o3nb99gddekhwo/gradle-8.10.2/bin/gradle"
export ANDROID_HOME="$HOME/Library/Android/sdk"
exec "$GRADLE_BIN" --no-daemon "$@"
