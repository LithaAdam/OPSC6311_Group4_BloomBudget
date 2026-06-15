package com.example.bloombudget

import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    fun getAllCategories(userId: String): Flow<List<Category>> = 
        categoryDao.getAllCategories(userId)

    fun insert(category: Category) {
        categoryDao.insertCategory(category)
    }

    fun update(category: Category) {
        categoryDao.updateCategory(category)
    }

    fun delete(category: Category) {
        categoryDao.deleteCategory(category)
    }
}
