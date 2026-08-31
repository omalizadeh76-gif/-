package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminMessageDao {

    @Query("SELECT * FROM admin_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<AdminMessage>>

    @Query("SELECT * FROM admin_messages WHERE isRead = 0 ORDER BY timestamp DESC LIMIT 1")
    fun getLatestUnreadMessage(): Flow<AdminMessage?>

    @Query("SELECT * FROM admin_messages ORDER BY timestamp DESC LIMIT 1")
    fun getLatestMessage(): Flow<AdminMessage?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AdminMessage): Long

    @Update
    suspend fun updateMessage(message: AdminMessage)

    @Delete
    suspend fun deleteMessage(message: AdminMessage)

    @Query("DELETE FROM admin_messages WHERE id = :id")
    suspend fun deleteMessageById(id: Int)

    @Query("DELETE FROM admin_messages")
    suspend fun clearAllMessages()

    @Query("UPDATE admin_messages SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)
}
