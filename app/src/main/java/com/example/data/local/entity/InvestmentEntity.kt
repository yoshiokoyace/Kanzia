package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val stock: String, // Ticker e.g. "KO"
    val amount: Double,
    val date: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis()
)
