# ProGuard / R8 Rules for LeanTV Launcher
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# Keep ViewModels and Bindings
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}

# Minimize R8 warnings
-dontwarn javax.annotation.**
-dontwarn java.lang.invoke.**

