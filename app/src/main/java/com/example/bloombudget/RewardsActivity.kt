package com.example.bloombudget

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bloombudget.databinding.ActivityRewardsBinding
import kotlin.getValue

class RewardsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRewardsBinding
    private val transactionViewModel: TransactionViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.bottomNavigation.selectedItemId = R.id.nav_rewards
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.nav_transactions -> {
                    startActivity(Intent(this, TransactionsActivity::class.java))
                    true
                }
                R.id.nav_statistics -> {
                    startActivity(Intent(this, StatisticsActivity::class.java))
                    true
                }
                R.id.nav_expenses -> {
                    startActivity(Intent(this, ExpensesActivity::class.java))
                    true
                }
                R.id.nav_rewards -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        observeData()
    }

    private fun observeData() {
        transactionViewModel.allTransactions.observe(this) { transactions ->
            categoryViewModel.allCategories.observe(this) { categories ->
                updateRewards(transactions, categories)
            }
        }
    }

    private fun updateRewards(transactions: List<Transaction>, categories: List<Category>) {
        // 1. Calculate Points
        // 10 pts per transaction
        var points = transactions.size * 10

        // 50 pts for Budget Master (if expenses < budget)
        val totalExp = Math.abs(transactions.filter { it.amount < 0 }.sumOf { it.amount })
        val totalBudget = categories.sumOf { it.budget }

        val isBudgetMaster = totalBudget > 0 && totalExp <= totalBudget
        if (isBudgetMaster) {
            points += 50
            binding.statusBudgetMaster.text = getString(R.string.label_unlocked)
            binding.statusBudgetMaster.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            binding.statusBudgetMaster.text = getString(R.string.label_locked)
            binding.statusBudgetMaster.setTextColor(getColor(android.R.color.holo_red_dark))
        }

        // 7 Day Streak Saver (Simulated: if user has more than 7 transactions)
        val isStreakSaver = transactions.size >= 7
        if (isStreakSaver) {
            points += 30
            binding.statusStreakSaver.text = getString(R.string.label_unlocked)
            binding.statusStreakSaver.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            binding.statusStreakSaver.text = getString(R.string.label_locked)
            binding.statusStreakSaver.setTextColor(getColor(android.R.color.holo_red_dark))
        }

        // Tax Deduction (if user has more than 20 transactions)
        val isTaxDeduction = transactions.size >= 20
        if (isTaxDeduction) {
            points += 100
            binding.statusTaxDeduction.text = getString(R.string.label_unlocked)
            binding.statusTaxDeduction.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            binding.statusTaxDeduction.text = getString(R.string.label_locked)
            binding.statusTaxDeduction.setTextColor(getColor(android.R.color.holo_red_dark))
        }

        // 2. Update UI
        binding.pointsValue.text = points.toString()

        // Leveling system: level = (points / 100) + 1
        val level = (points / 100) + 1
        val progress = points % 100

        binding.userLevel.text = getString(R.string.format_level, level)
        binding.levelProgress.progress = progress
    }
}