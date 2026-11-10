# Preserve line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep native methods and classes accessed via JNI
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep all classes in com.froop.app.kegs package for JNI access
-keep class com.froop.app.kegs.** { *; }
-keepclassmembers class com.froop.app.kegs.** { *; }
-keepclassmembers class com.froop.app.kegs.Event$* { *; }
