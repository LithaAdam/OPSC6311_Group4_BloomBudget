package com.example.bloombudget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bloombudget.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Set user profile info
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val name = sharedPref.getString("USER_NAME", getString(R.string.default_user_name))
        binding.userName.text = name
        binding.userEmail.text = sharedPref.getString("USER_EMAIL", getString(R.string.default_user_email))
        
        // Set avatar letter
        if (!name.isNullOrEmpty()) {
            binding.avatarLetter.text = name.take(1).uppercase()
        }

        binding.profileCard.setOnClickListener {
            startActivity(Intent(this, AccountSettingsActivity::class.java))
        }

        setupSettingsOptions()
        setupBottomNavigation()
    }

    private fun setupSettingsOptions() {
        // About Dialog
        binding.aboutCard.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.label_about)
                .setMessage(R.string.about_description)
                .setPositiveButton(R.string.btn_close, null)
                .show()
        }

        // Sign Out Logic
        binding.signOutCard.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.label_sign_out)
                .setMessage(R.string.msg_sign_out_confirm)
                .setPositiveButton(R.string.btn_yes) { _, _ ->
                    // Clear session
                    val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    sharedPref.edit().remove("LOGGED_IN_USER_EMAIL").apply()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton(R.string.btn_no, null)
                .show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_settings
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
                R.id.nav_rewards -> {
                    startActivity(Intent(this, RewardsActivity::class.java))
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }
}