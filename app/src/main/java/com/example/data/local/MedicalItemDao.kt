package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ItemCategory
import com.example.data.model.MedicalItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalItemDao {

    @Query("SELECT * FROM medical_items ORDER BY CASE WHEN hasExpiryDate = 1 THEN expiryTimestamp ELSE 9223372036854775807 END ASC, id DESC")
    fun getAllItems(): Flow<List<MedicalItem>>

    @Query("SELECT * FROM medical_items ORDER BY CASE WHEN hasExpiryDate = 1 THEN expiryTimestamp ELSE 9223372036854775807 END ASC, id DESC")
    suspend fun getAllItemsList(): List<MedicalItem>

    @Query("SELECT * FROM medical_items WHERE category = :category ORDER BY expiryTimestamp ASC")
    fun getItemsByCategory(category: ItemCategory): Flow<List<MedicalItem>>

    @Query("SELECT * FROM medical_items WHERE id = :id")
    suspend fun getItemById(id: Int): MedicalItem?

    @Query("SELECT * FROM medical_items WHERE name LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' ORDER BY expiryTimestamp ASC")
    fun searchItems(query: String): Flow<List<MedicalItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MedicalItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MedicalItem>)

    @Update
    suspend fun updateItem(item: MedicalItem)

    @Delete
    suspend fun deleteItem(item: MedicalItem)

    @Query("DELETE FROM medical_items WHERE id = :id")
    suspend fun deleteItemById(id: Int)

    @Query("SELECT COUNT(*) FROM medical_items")
    suspend fun getCount(): Int
}
