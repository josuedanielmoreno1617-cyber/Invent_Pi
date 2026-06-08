package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryLogDao {
    @Query("SELECT * FROM inventory_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<InventoryLog>>

    @Insert
    suspend fun insertLog(log: InventoryLog)
    
    @Insert
    suspend fun insertLogs(logs: List<InventoryLog>)
}
