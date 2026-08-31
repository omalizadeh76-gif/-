package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.InspectionRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface InspectionDao {
    @Query("SELECT * FROM inspection_history ORDER BY timestamp DESC")
    fun getAllInspections(): Flow<List<InspectionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(record: InspectionRecord): Long

    @Query("DELETE FROM inspection_history WHERE id = :id")
    suspend fun deleteInspection(id: Int)

    @Query("DELETE FROM inspection_history")
    suspend fun clearAllHistory()
}
