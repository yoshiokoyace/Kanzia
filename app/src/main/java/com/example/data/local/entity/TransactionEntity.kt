package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "income" or "expense"
    val amount: Double,
    val category: String,
    val note: String = "",
    val date: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis()
)
