# Vaddi Calculator Production ProGuard / R8 Rules

-keepattributes SourceFile,LineNumberTable

# Room Database
-keep class androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Application Data Models and Entities
-keep class com.vaddicalculatortool.app.data.local.** { *; }
-keep class com.vaddicalculatortool.app.domain.model.** { *; }
-keepclassmembers class com.vaddicalculatortool.app.data.local.** { *; }
-keepclassmembers class com.vaddicalculatortool.app.domain.model.** { *; }

