# Add project specific ProGuard rules here.

# Keep Room entity classes and their fields
-keep class com.taskflow.todo.data.entity.** { *; }

# Keep Room DAO classes
-keep interface com.taskflow.todo.data.dao.** { *; }

# Keep Room Database class
-keep class com.taskflow.todo.data.db.** { *; }

# Room uses reflection for column names; keep field annotations
-keepattributes *Annotation*

# Suppress warnings for Room-generated classes
-dontwarn androidx.room.**
