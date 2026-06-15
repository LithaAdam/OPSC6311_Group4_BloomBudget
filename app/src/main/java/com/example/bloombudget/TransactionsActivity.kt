package com.example.bloombudget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bloombudget.databinding.ActivityTransactionsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * TransactionsActivity displays a detailed list of all user transactions using a RecyclerView.
 */
class TransactionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionsBinding
    private val viewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupBottomNavigation()
        observeViewModel()
        setupGoalTracking()
        updateCurrentDate()

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun updateCurrentDate() {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        binding.currentDateText.text = "Today - ${sdf.format(Date())}"
    }

    private fun setupGoalTracking() {
        val userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = userPrefs.getString("LOGGED_IN_USER_EMAIL", "") ?: "default"
        val prefs = getSharedPreferences("GoalPrefs_$userId", Context.MODE_PRIVATE)

        binding.minGoalInput.setText(prefs.getString("min_goal", ""))
        binding.maxGoalInput.setText(prefs.getString("max_goal", ""))

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                prefs.edit().apply {
                    putString("min_goal", binding.minGoalInput.text.toString())
                    putString("max_goal", binding.maxGoalInput.text.toString())
                    apply()
                }
            }
        }

        binding.minGoalInput.addTextChangedListener(textWatcher)
        binding.maxGoalInput.addTextChangedListener(textWatcher)
    }

    private fun setupRecyclerView() {
        binding.transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        viewModel.allTransactions.observe(this) { transactions ->
            binding.transactionsRecyclerView.adapter = TransactionAdapter(transactions)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_transactions
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.nav_transactions -> true
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
