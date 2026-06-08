package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_logs")
data class InventoryLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val productCode: String,
    val productName: String,
    val type: String, // "CREACIÓN", "ENTRADA", "SALIDA", "MODIFICACIÓN"
    val quantityChange: Int,
    val timestamp: Long,
    val details: String
)
