# Common
-keepattributes *Annotation*

# http://qiita.com/GeneralD/items/759840150996de230c37
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Gson
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

# OkHttp & picasso
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Otto
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

# AndroidAnnotation
-dontwarn org.androidannotations.api.rest.**

# Annotations
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

-keep class com.mobiapp.nicewallpapers.model.** { *; }