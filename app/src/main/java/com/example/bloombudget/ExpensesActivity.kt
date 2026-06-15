package com.example.bloombudget

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bloombudget.databinding.ActivityExpensesBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpensesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpensesBinding
    private val viewModel: CategoryViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.addCategoryButton.setOnClickListener {
            showAddCategoryDialog()
        }

        binding.addTransactionButton.setOnClickListener {
            showAddTransactionDialog()
        }

        setupRecyclerView()
        setupBottomNavigation()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        viewModel.allCategories.observe(this) { categories ->
            binding.categoriesRecyclerView.adapter = CategoryAdapter(categories)
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val categoryNameInput = dialogView.findViewById<AutoCompleteTextView>(R.id.categoryNameInput)
        val budgetInput = dialogView.findViewById<EditText>(R.id.budgetInput)

        // Predefined suggested categories
        val suggestions = arrayOf("Food", "Transport", "Shopping", "Entertainment", "Bills", "Health", "Education", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        categoryNameInput.setAdapter(adapter)

        AlertDialog.Builder(this)
            .setTitle(R.string.label_add_category)
            .setView(dialogView)
            .setPositiveButton(R.string.btn_yes) { _, _ ->
                val name = categoryNameInput.text.toString()
                val budgetStr = budgetInput.text.toString()

                if (name.isNotEmpty() && budgetStr.isNotEmpty()) {
                    val budget = budgetStr.toDoubleOrNull() ?: 0.0
                    val iconResId = when(name.lowercase()) {
                        "food" -> R.drawable.food_icon
                        "transport" -> R.drawable.transport_icon
                        "shopping" -> R.drawable.shopping_icon
                        "entertainment" -> R.drawable.game_icon
                        "bills" -> R.drawable.money_bag_icon
                        else -> R.drawable.ic_dashboard
                    }
                    val category = Category(
                        name = name,
                        budget = budget,
                        iconResId = iconResId
                    )
                    viewModel.insert(category)
                    Toast.makeText(this, R.string.msg_category_added, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, R.string.error_invalid_input, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.btn_no, null)
            .show()
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

        // Populate categories from database to make it efficient and user friendly
        viewModel.allCategories.observe(this) { categories ->
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
                        amount = -amount,
                        category = category,
                        date = date,
                        iconResId = iconResId
                    )
                    transactionViewModel.insert(transaction)
                    Toast.makeText(this, R.string.msg_transaction_added, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, R.string.error_invalid_input, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_expenses
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
                R.id.nav_expenses -> true
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
