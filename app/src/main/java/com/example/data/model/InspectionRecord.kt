package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inspection_history")
data class InspectionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val totalChecked: Int = 0,
    val expiredCount: Int = 0,
    val expiringSoonCount: Int = 0,
    val safeCount: Int = 0,
    val jumpBagMedicineCount: Int = 0,
    val medicineCabinetCount: Int = 0,
    val jumpBagEquipmentCount: Int = 0,
    val equipmentCabinetCount: Int = 0,
    val detailsSummary: String = "", // خلاصه‌ای از وضعیت اقلام
    val statusNote: String = "بررسی کامل انجام شد"
)
