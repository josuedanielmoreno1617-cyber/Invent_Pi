package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productNumber: String = "",
    val code: String,
    val name: String,
    val description: String = "",
    val quantity: Int,
    val unitsPerBox: Int = 0,
    val unitsPerBulk: Int = 0,
    val buyPrice: Double,
    val sellPrice: Double,
    val dateJoined: Long,
    val location: String
)
