# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Gson
-keep class com.huck.biblequiz.data.remote.dto.** { *; }

# Room entities
-keep class com.huck.biblequiz.data.local.entity.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
