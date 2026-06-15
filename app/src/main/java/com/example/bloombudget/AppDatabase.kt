package com.example.bloombudget

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Transaction::class, Category::class, User::class, SavingsGoal::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
    abstract fun savingsGoalDao(): SavingsGoalDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Default categories are now added per user during registration
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bloom_budget_database"
                )
                .addCallback(AppDatabaseCallback(CoroutineScope(Dispatchers.IO)))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        fun populateDefaultCategories(categoryDao: CategoryDao, userId: String) {
            val categories = listOf(
                Category("Food", userId, 500.0, R.drawable.food_icon),
                Category("Transport", userId, 200.0, R.drawable.transport_icon),
                Category("Shopping", userId, 300.0, R.drawable.shopping_icon),
                Category("Entertainment", userId, 150.0, R.drawable.game_icon),
                Category("Bills", userId, 1000.0, R.drawable.money_bag_icon)
            )
            for (category in categories) {
                categoryDao.insertCategory(category)
            }
        }
    }
}
