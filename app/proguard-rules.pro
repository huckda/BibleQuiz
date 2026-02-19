# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses,EnclosingMethod
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Keep Retrofit service interfaces with full generic signatures
-keep,allowobfuscation interface com.huck.biblequiz.data.remote.BollsApiService { *; }

# Gson
-keep class com.huck.biblequiz.data.remote.dto.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Room entities
-keep class com.huck.biblequiz.data.local.entity.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
