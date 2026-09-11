# Vaddi Calculator Production ProGuard / R8 Rules

-keepattributes SourceFile,LineNumberTable

# Room Database
-keep class androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Application Data Models and Entities
-keep class com.vaddicalculator.app.data.local.** { *; }
-keep class com.vaddicalculator.app.domain.model.** { *; }
-keepclassmembers class com.vaddicalculator.app.data.local.** { *; }
-keepclassmembers class com.vaddicalculator.app.domain.model.** { *; }

