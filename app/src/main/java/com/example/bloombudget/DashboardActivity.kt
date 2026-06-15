package com.example.bloombudget

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bloombudget.databinding.ActivityDashboardBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * DashboardActivity displays the user's financial overview using ViewBinding and RecyclerView.
 */
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: TransactionViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private val savingsGoalDao: SavingsGoalDao by lazy { AppDatabase.getDatabase(this).savingsGoalDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set user name from SharedPreferences
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("USER_NAME", "User") ?: "User"
        binding.hiText.text = getString(R.string.format_hi_user, userName)

        setupRecyclerView()
        setupBottomNavigation()
        observeViewModel()
        observeSavingsGoals()
        checkAIAlerts()

        binding.seeAllLink.setOnClickListener {
            startActivity(Intent(this, TransactionsActivity::class.java))
        }

        binding.addExpenseButton.setOnClickListener {
            showAddTransactionDialog()
        }

        binding.addGoalButton.setOnClickListener {
            showAddGoalDialog()
        }

        binding.addCategoryButton.setOnClickListener {
            // Re-using the logic from ExpensesActivity or navigating there
            startActivity(Intent(this, ExpensesActivity::class.java))
        }

        binding.captureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun observeSavingsGoals() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("USER_EMAIL", "") ?: ""
        
        lifecycleScope.launch {
            savingsGoalDao.getAllGoals(email).collectLatest { goals ->
                binding.savingsGoalsRecyclerView.adapter = SavingsGoalAdapter(goals)
                binding.savingsGoalsRecyclerView.layoutManager = LinearLayoutManager(this@DashboardActivity, LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }

    private fun showAddGoalDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_transaction, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.titleInput)
        val amountInput = dialogView.findViewById<EditText>(R.id.amountInput)
        
        titleInput.hint = getString(R.string.hint_goal_title)
        amountInput.hint = getString(R.string.hint_target_amount)
        dialogView.findViewById<android.view.View>(R.id.categoryInput).visibility = android.view.View.GONE
        dialogView.findViewById<android.view.View>(R.id.dateInput).visibility = android.view.View.GONE

        AlertDialog.Builder(this)
            .setTitle(R.string.title_savings_goals)
            .setView(dialogView)
            .setPositiveButton(R.string.btn_register) { _: DialogInterface, _: Int ->
                val title = titleInput.text.toString()
                val targetStr = amountInput.text.toString()
                if (title.isNotEmpty() && targetStr.isNotEmpty()) {
                    val target = targetStr.toDoubleOrNull() ?: 0.0
                    val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val email = sharedPref.getString("USER_EMAIL", "") ?: ""
                    
                    lifecycleScope.launch {
                        savingsGoalDao.insertGoal(SavingsGoal(userId = email, title = title, targetAmount = target))
                    }
                }
            }
            .setNegativeButton(R.string.btn_close, null)
            .show()
    }

    private fun checkAIAlerts() {
        viewModel.allTransactions.observe(this) { transactions ->
            if (transactions.isEmpty()) return@observe
            
            val expenses = transactions.filter { it.amount < 0 }.map { Math.abs(it.amount) }
            if (expenses.size < 3) return@observe

            val avgDailySpending = expenses.sum() / 30.0 
            val recentSpending = expenses.take(3).sum() / 3.0 
            
            if (recentSpending > avgDailySpending * 1.5) {
                binding.aiAlertCard.visibility = android.view.View.VISIBLE
                binding.aiAlertMessage.text = getString(R.string.msg_ai_alert_high_spending, 5)
            } else {
                binding.aiAlertCard.visibility = android.view.View.GONE
            }
        }
    }

    private val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Image captured! In a real app, you'd save the bitmap or file path.
            android.widget.Toast.makeText(this, "Image Captured and Stored!", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddTransactionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_transaction, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.titleInput)
        val amountInput = dialogView.findViewById<EditText>(R.id.amountInput)
        val categoryInput = dialogView.findViewById<AutoCompleteTextView>(R.id.categoryInput)
        val dateInput = dialogView.findViewById<EditText>(R.id.dateInput)

        // Set default date
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        dateInput.setText(sdf.format(Date()))

        dateInput.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH)
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

            android.app.DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                dateInput.setText(sdf.format(calendar.time))
            }, year, month, day).show()
        }

        // Populate categories
        categoryViewModel.allCategories.observe(this) { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryNames)
            categoryInput.setAdapter(adapter)
        }

        AlertDialog.Builder(this)
            .setTitle("Add Transaction")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString()
                val amountStr = amountInput.text.toString()
                val category = categoryInput.text.toString()
                val date = dateInput.text.toString()

                if (title.isNotEmpty() && amountStr.isNotEmpty() && category.isNotEmpty() && date.isNotEmpty()) {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0

                    val iconResId = when(category.lowercase()) {
                        "food" -> R.drawable.food_icon
                        "transport" -> R.drawable.transport_icon
                        "shopping" -> R.drawable.shopping_icon
                        "entertainment" -> R.drawable.game_icon
                        "bills" -> R.drawable.money_bag_icon
                        else -> R.drawable.ic_dashboard
                    }
                    val transaction = Transaction(
                        title = title,
                        amount = -amount, // Assuming these are expenses
                        category = category,
                        date = date,
                        iconResId = iconResId
                    )
                    viewModel.insert(transaction)
                    android.widget.Toast.makeText(this, R.string.msg_transaction_added, android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    android.widget.Toast.makeText(this, R.string.error_invalid_input, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupRecyclerView() {
        binding.recentExpensesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        viewModel.allTransactions.observe(this) { transactions ->
            // Show only the most recent few transactions on the dashboard
            val recentTransactions = transactions.take(5)
            binding.recentExpensesRecyclerView.adapter = TransactionAdapter(recentTransactions)

            val totalBalance = transactions.sumOf { it.amount }
            binding.balanceAmount.text = getString(R.string.format_amount_positive, totalBalance)

            updateBudgetProgress()
        }

        categoryViewModel.allCategories.observe(this) {
            updateBudgetProgress()
        }

        viewModel.totalIncome.observe(this) { income ->
            // Update UI with total income if needed
        }
    }

    private fun updateBudgetProgress() {
        val transactions = viewModel.allTransactions.value ?: emptyList()
        val categories = categoryViewModel.allCategories.value ?: emptyList()

        val totalExp = Math.abs(transactions.filter { it.amount < 0 }.sumOf { it.amount })
        val totalBudget = categories.sumOf { it.budget }

        if (totalBudget > 0) {
            binding.budgetRatio.text = getString(R.string.format_budget_ratio, totalExp, totalBudget)
            val progress = ((totalExp / totalBudget) * 100).toInt().coerceAtMost(100)
            binding.budgetProgressBar.progress = progress
            binding.budgetPercentage.text = getString(R.string.format_percentage, progress.toDouble())
        } else {
            binding.budgetRatio.text = getString(R.string.format_budget_ratio, totalExp, 0.0)
            binding.budgetProgressBar.progress = 0
            binding.budgetPercentage.text = getString(R.string.placeholder_percentage)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_dashboard
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true
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
