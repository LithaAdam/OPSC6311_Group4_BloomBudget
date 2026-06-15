package com.example.bloombudget

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: String? = null
)
