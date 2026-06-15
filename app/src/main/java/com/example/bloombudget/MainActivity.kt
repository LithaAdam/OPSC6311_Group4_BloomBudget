package com.example.bloombudget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * MainActivity serves as the entry point (Splash Screen) for the BloomBudget app.
 * It provides options for new users to "Get Started" or existing users to "Login".
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display for a modern look
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Adjust padding to account for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize "Get Started" button and navigate to Registration screen
        val getStartedButton: Button = findViewById(R.id.get_started_button)
        getStartedButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        // Initialize "Login" text link and navigate to Log in screen
        val loginText: TextView = findViewById(R.id.login_text)
        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}