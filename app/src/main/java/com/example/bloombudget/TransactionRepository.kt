package com.example.bloombudget

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    fun getAllTransactions(userId: String): Flow<List<Transaction>> = 
        transactionDao.getAllTransactions(userId)
    
    fun getTotalIncome(userId: String): Flow<Double?> = 
        transactionDao.getTotalIncome(userId)
    
    fun getTotalExpenses(userId: String): Flow<Double?> = 
        transactionDao.getTotalExpenses(userId)

    fun insert(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    fun update(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    fun delete(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }
}
