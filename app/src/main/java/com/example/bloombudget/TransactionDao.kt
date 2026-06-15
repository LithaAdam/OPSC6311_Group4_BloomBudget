package com.example.bloombudget

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getAllTransactions(userId: String): Flow<List<Transaction>>

    @Insert
    fun insertTransaction(transaction: Transaction)

    @Update
    fun updateTransaction(transaction: Transaction)

    @Delete
    fun deleteTransaction(transaction: Transaction)

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND amount > 0")
    fun getTotalIncome(userId: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND amount < 0")
    fun getTotalExpenses(userId: String): Flow<Double?>

    @Query("DELETE FROM transactions WHERE userId = :userId")
    fun deleteAllTransactions(userId: String)
}
