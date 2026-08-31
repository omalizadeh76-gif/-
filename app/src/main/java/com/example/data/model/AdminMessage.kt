package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_messages")
data class AdminMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val priority: String = "HIGH", // "NORMAL", "HIGH", "CRITICAL"
    val targetCategory: String = "ALL", // "ALL", "MEDICINE", "EQUIPMENT", "JUMPBAG"
    val senderName: String = "مدیریت فوریت‌ها",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
