package com.example.bloombudget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * LoginActivity handles user authentication.
 * It allows users to log in to their account or navigate to the registration screen.
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInput: EditText = findViewById(R.id.email_input)
        val loginButton: Button = findViewById(R.id.login_button)
        val passwordInput: EditText = findViewById(R.id.password_input)
        
        // Handle Login Button click: Navigate to the Dashboard
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val database = AppDatabase.getDatabase(this)
            CoroutineScope(Dispatchers.IO).launch {
                val user = database.userDao().getUserByEmail(email)
                
                withContext(Dispatchers.Main) {
                    if (user != null && user.password == password) {
                        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("LOGGED_IN_USER_EMAIL", email)
                            // Update display names for UI convenience
                            putString("USER_NAME", user.name)
                            putString("USER_SURNAME", user.surname)
                            putString("USER_EMAIL", user.email)
                            apply()
                        }
                        
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Handle SignUp Text link click: Navigate to the Registration screen
        val signupText: TextView = findViewById(R.id.register_link)
        signupText.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}