package com.example.reminders.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.reminders.data.model.Category
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderTypeConverter {
    @TypeConverter
    fun fromType(type: ReminderType): String = type.name
    @TypeConverter
    fun toType(value: String): ReminderType = ReminderType.valueOf(value)
}

@Database(entities = [Reminder::class, Category::class], version = 2, exportSchema = false)
@TypeConverters(ReminderTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE categories ADD COLUMN continuous INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE reminders ADD COLUMN continuous INTEGER DEFAULT NULL")
            }
        }

        fun seedCallback() = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                CoroutineScope(Dispatchers.IO).launch {
                    db.execSQL("""
                        INSERT INTO categories (name, color, sound, volume, continuous) VALUES
                        ('משפחה', 'accent-500', 'chime', 70, 0),
                        ('בית', 'accent-2-500', 'birds', 60, 0),
                        ('בריאות', 'accent-300', 'marimba', 50, 0),
                        ('כספים', 'neutral-600', 'bell', 80, 1)
                    """.trimIndent())
                }
            }
        }
    }
}
