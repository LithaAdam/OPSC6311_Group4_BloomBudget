package com.example.bloombudget

import androidx.room.Entity

@Entity(tableName = "categories", primaryKeys = ["name", "userId"])
data class Category(
    val name: String,
    val userId: String = "",
    val budget: Double,
    val iconResId: Int
)
