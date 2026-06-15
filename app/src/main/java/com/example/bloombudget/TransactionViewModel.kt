package com.example.bloombudget

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository
    val allTransactions: LiveData<List<Transaction>>
    val totalIncome: LiveData<Double?>
    val totalExpenses: LiveData<Double?>
    val userId: String

    init {
        val sharedPref = application.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPref.getString("LOGGED_IN_USER_EMAIL", "") ?: ""
        
        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(transactionDao)
        
        allTransactions = repository.getAllTransactions(userId).asLiveData()
        totalIncome = repository.getTotalIncome(userId).asLiveData()
        totalExpenses = repository.getTotalExpenses(userId).asLiveData()
    }

    fun insert(transaction: Transaction) = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
        repository.insert(transaction.copy(userId = userId))
    }

    fun update(transaction: Transaction) = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
        repository.update(transaction)
    }

    fun delete(transaction: Transaction) = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
        repository.delete(transaction)
    }
}
