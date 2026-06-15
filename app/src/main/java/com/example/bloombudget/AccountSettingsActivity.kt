package com.example.bloombudget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AccountSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        val backButton: ImageButton = findViewById(R.id.back_button)
        val logoutIcon: ImageView = findViewById(R.id.logout_icon)
        
        val displayFullName: TextView = findViewById(R.id.display_full_name)
        val valueFirstName: TextView = findViewById(R.id.value_first_name)
        val valueLastName: TextView = findViewById(R.id.value_last_name)
        val valueEmail: TextView = findViewById(R.id.value_email)

        // Load data from SharedPreferences
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val firstName = sharedPref.getString("USER_NAME", "") ?: ""
        val lastName = sharedPref.getString("USER_SURNAME", "") ?: ""
        val email = sharedPref.getString("USER_EMAIL", "") ?: ""

        // Set values to UI
        displayFullName.text = "$firstName $lastName"
        valueFirstName.text = firstName
        valueLastName.text = lastName
        valueEmail.text = email

        backButton.setOnClickListener {
            finish()
        }

        logoutIcon.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.label_sign_out)
            .setMessage(R.string.msg_sign_out_confirm)
            .setPositiveButton(R.string.btn_yes) { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton(R.string.btn_no, null)
            .show()
    }
}