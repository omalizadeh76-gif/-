package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.AdminMessage
import com.example.data.model.InspectionRecord
import com.example.data.model.ItemCategory
import com.example.data.model.MedicalItem

class Converters {
    @TypeConverter
    fun fromCategory(category: ItemCategory?): String {
        return category?.name ?: ItemCategory.MEDICINE.name
    }

    @TypeConverter
    fun toCategory(value: String?): ItemCategory {
        return try {
            if (value != null) ItemCategory.valueOf(value) else ItemCategory.MEDICINE
        } catch (e: Exception) {
            ItemCategory.MEDICINE
        }
    }
}

@Database(
    entities = [MedicalItem::class, InspectionRecord::class, AdminMessage::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicalItemDao(): MedicalItemDao
    abstract fun inspectionDao(): InspectionDao
    abstract fun adminMessageDao(): AdminMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medical_inventory.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
