package com.example.bloombudget

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CategoryRepository
    val allCategories: LiveData<List<Category>>
    val userId: String

    init {
        val sharedPref = application.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPref.getString("LOGGED_IN_USER_EMAIL", "") ?: ""
        
        val categoryDao = AppDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(categoryDao)
        allCategories = repository.getAllCategories(userId).asLiveData()
    }

    fun insert(category: Category) = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
        repository.insert(category.copy(userId = userId))
    }

    fun update(category: Category) = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
        repository.update(category)
    }

    fun delete(category: Category) = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
        repository.delete(category)
    }
}
