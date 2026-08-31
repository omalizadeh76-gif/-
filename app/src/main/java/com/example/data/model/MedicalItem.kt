package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.concurrent.TimeUnit

enum class ItemCategory {
    MEDICINE,   // دارو
    EQUIPMENT   // تجهیزات
}

enum class ExpiryStatus {
    EXPIRED,        // منقضی شده (Red)
    EXPIRING_SOON,  // در آستانه انقضا - کمتر از ۳۰ روز (Orange)
    SAFE,           // دارای اعتبار بیش از ۳۰ روز (Green)
    NO_EXPIRY       // بدون تاریخ انقضا (برای تجهیزات دائمی)
}

@Entity(tableName = "medical_items")
data class MedicalItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: ItemCategory = ItemCategory.MEDICINE,
    val hasExpiryDate: Boolean = true,
    val expiryTimestamp: Long = 0L, // Epoch milliseconds at end of day
    val quantity: Int = 1,
    val unit: String = "بسته", // بسته، جعبه، عدد، ورق، ویال، شیشه، اسپری، تیوب
    val location: String = "کمد داروها", // کمد دارو، یخچال، جعبه کمک‌های اولیه، کیف
    val notes: String = "",
    val reminderDaysBefore: Int = 30, // Default ~1 month (30 days)
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Calculates remaining days from current time.
     * Positive = days left, Negative = expired days ago.
     */
    fun getRemainingDays(currentTimeMs: Long = System.currentTimeMillis()): Long {
        if (!hasExpiryDate || expiryTimestamp <= 0L) return Long.MAX_VALUE
        val diffMs = expiryTimestamp - currentTimeMs
        return TimeUnit.MILLISECONDS.toDays(diffMs)
    }

    /**
     * Determines whether the item is expired, expiring soon (< reminderDaysBefore days), or safe.
     */
    fun getExpiryStatus(currentTimeMs: Long = System.currentTimeMillis()): ExpiryStatus {
        if (!hasExpiryDate || expiryTimestamp <= 0L) return ExpiryStatus.NO_EXPIRY
        val days = getRemainingDays(currentTimeMs)
        return when {
            days < 0 -> ExpiryStatus.EXPIRED
            days <= reminderDaysBefore -> ExpiryStatus.EXPIRING_SOON
            else -> ExpiryStatus.SAFE
        }
    }

    /**
     * Checks if this item needs notification (i.e. Expired or Expiring Soon).
     */
    fun isExpiringOrExpired(currentTimeMs: Long = System.currentTimeMillis()): Boolean {
        if (!hasExpiryDate || expiryTimestamp <= 0L) return false
        val status = getExpiryStatus(currentTimeMs)
        return status == ExpiryStatus.EXPIRING_SOON || status == ExpiryStatus.EXPIRED
    }
}
