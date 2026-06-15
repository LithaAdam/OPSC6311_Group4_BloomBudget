package com.example.bloombudget

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val name: String,
    val surname: String,
    val password: String,
    val address: String,
    val dob: String
)
