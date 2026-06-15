package com.example.bloombudget

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bloombudget.databinding.ActivityStatisticsBinding
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * StatisticsActivity provides a visual summary of the user's spending habits.
 * It includes a spending breakdown by category and a monthly summary.
 */
class StatisticsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticsBinding
    private val viewModel: TransactionViewModel by viewModels()
    private var currentSelectedDate = java.util.Calendar.getInstance()

    private fun getCategoryColor(category: String): Int {
        val colorResName = "cat_${category.lowercase()}"
        val resId = resources.getIdentifier(colorResName, "color", packageName)
        return if (resId != 0) {
            ContextCompat.getColor(this, resId)
        } else {
            ContextCompat.getColor(this, R.color.subtitle_gray)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listener for the back button
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        setupBottomNavigation()
        observeViewModel()
        updateCurrentDate()

        binding.monthSelector.setOnClickListener {
            showMonthYearPicker()
        }
    }

    private fun showMonthYearPicker() {
        val year = currentSelectedDate[java.util.Calendar.YEAR]
        val month = currentSelectedDate[java.util.Calendar.MONTH]

        android.app.DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, _ ->
                currentSelectedDate.set(selectedYear, selectedMonth, 1)
                updateCurrentDate()
                observeViewModel()
            },
            year,
            month,
            1
        ).show()
    }

    private fun setupRecyclerView() {
        binding.categoryStatisticsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun updateCurrentDate() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding.selectedMonthText.text = sdf.format(currentSelectedDate.time)
    }

    private fun observeViewModel() {
        viewModel.allTransactions.observe(this) { allTransactions ->
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val targetMonthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentSelectedDate.time)

            val monthlyTransactions = allTransactions.filter { 
                try {
                    val date = sdf.parse(it.date)
                    val monthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(date!!)
                    monthYear == targetMonthYear
                } catch (_: Exception) {
                    false
                }
            }

            val expenses = monthlyTransactions.filter { it.amount < 0 }
            val totalExp = kotlin.math.abs(expenses.sumOf { it.amount })

            binding.totalSpentAmount.text = getString(R.string.format_amount_positive, totalExp)

            if (totalExp > 0) {
                val categoryMap = expenses.groupBy { it.category.lowercase() }

                val stats = categoryMap.map { (category, list) ->
                    val sum = kotlin.math.abs(list.sumOf { it.amount })
                    val percentage = (sum / totalExp) * 100

                    val colorResId = when(category) {
                        "food" -> R.drawable.dot_pink
                        "transport" -> R.drawable.dot_blue
                        "shopping" -> R.drawable.dot_purple
                        "entertainment" -> R.drawable.dot_green
                        else -> R.drawable.dot_blue
                    }

                    CategoryStatistic(category.replaceFirstChar { it.uppercase() }, sum, percentage, colorResId)
                }

                updateDonutChart(categoryMap, totalExp)
                binding.categoryStatisticsRecyclerView.adapter = CategoryStatisticAdapter(stats)
            } else {
                binding.totalSpentAmount.text = getString(R.string.placeholder_balance)
                binding.categoryStatisticsRecyclerView.adapter = CategoryStatisticAdapter(emptyList())
                updateDonutChart(emptyMap(), 0.0)
            }
        }
    }

    private fun updateDonutChart(categoryMap: Map<String, List<Transaction>>, totalExp: Double) {
        val segments = categoryMap.mapNotNull { (category, list) ->
            val sum = kotlin.math.abs(list.sumOf { it.amount })
            val percentage = if (totalExp > 0) (sum / totalExp) * 100 else 0.0

            if (percentage > 0) {
                val color = getCategoryColor(category)
                DonutChartView.ChartSegment(category, sum, percentage, color)
            } else null
        }

        binding.donutChartView.setData(segments)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_statistics
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
                R.id.nav_statistics -> true
                R.id.nav_expenses -> {
                    startActivity(Intent(this, ExpensesActivity::class.java))
                    true
                }
                R.id.nav_rewards -> {
                    startActivity(Intent(this, RewardsActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}