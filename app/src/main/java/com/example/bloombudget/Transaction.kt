package com.example.bloombudget

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String = "",
    val title: String,
    val date: String,
    val amount: Double,
    val category: String,
    val iconResId: Int
)
