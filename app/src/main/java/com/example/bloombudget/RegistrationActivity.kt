package com.example.bloombudget

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * RegistrationActivity allows new users to create an account.
 * Currently, it simulates registration by validating that fields are not empty.
 */
class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val nameEditText = findViewById<EditText>(R.id.edit_name)
        val surnameEditText = findViewById<EditText>(R.id.edit_surname)
        val emailEditText = findViewById<EditText>(R.id.edit_email)
        val passwordEditText = findViewById<EditText>(R.id.edit_password)
        val registerButton = findViewById<AppCompatButton>(R.id.register_button)
        val loginText = findViewById<TextView>(R.id.have_account_text)
        val addressEditText = findViewById<EditText>(R.id.edit_address)
        val dobEditText = findViewById<EditText>(R.id.edit_dob) //dob = date of birth

        //examples for the user for each input field
        nameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                nameEditText.hint = getString(R.string.example_name)
            }
            else {
                nameEditText.hint = getString(R.string.hint_name)
            }
        }

        surnameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                surnameEditText.hint = getString(R.string.example_surname)
            }
            else {
                surnameEditText.hint = getString(R.string.hint_surname)
            }
        }

        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                emailEditText.hint = getString(R.string.example_email)
            }
            else {
                emailEditText.hint = getString(R.string.hint_email)
            }
        }

        addressEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                addressEditText.hint = getString(R.string.example_address)
            }
            else {
                addressEditText.hint = getString(R.string.hint_address)
            }
        }

        // Handle Registration button click
        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val surname = surnameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val dob = dobEditText.text.toString().trim()

            //new
            val namePattern = Regex("^[A-Za-z \\-]+$") // Allows letters, spaces, and hyphens
            val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$") // Allows any character except whitespace

            if (name.isNotEmpty() && surname.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
                name.matches(namePattern) && surname.matches(namePattern) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length >= 8 && password.matches(passwordPattern) &&
                address.isNotEmpty() && dob.isNotEmpty()
            ) {
                // Save user info in SharedPreferences
                val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("USER_NAME", name)
                    putString("USER_SURNAME", surname)
                    putString("USER_EMAIL", email)
                    putString("USER_PASSWORD", password)
                    putString("USER_ADDRESS", address)
                    putString("USER_DOB", dob)
                    // Also set as logged in user
                    putString("LOGGED_IN_USER_EMAIL", email)
                    apply()
                }

                // Save user to Database and setup default categories
                val database = AppDatabase.getDatabase(this)
                val newUser = User(email, name, surname, password, address, dob)
                
                CoroutineScope(Dispatchers.IO).launch {
                    database.userDao().insertUser(newUser)
                    // Initialize default categories for this specific new user
                    AppDatabase.populateDefaultCategories(database.categoryDao(), email)
                }

                Toast.makeText(this, getString(R.string.msg_registration_success), Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.error_fix_errors), Toast.LENGTH_SHORT).show()
                //error messages for each field
                //name field
                if (name.isEmpty()) {
                    nameEditText.error = getString(R.string.error_fill_name)
                } else if (!name.matches(namePattern)) {
                    nameEditText.error = getString(R.string.error_name_letters)
                }

                //surname field
                if (surname.isEmpty()) {
                    surnameEditText.error = getString(R.string.error_fill_surname)
                } else if (!surname.matches(namePattern)) {
                    surnameEditText.error = getString(R.string.error_surname_letters)
                }

                //email filed
                if (email.isEmpty()) {
                    emailEditText.error = getString(R.string.error_fill_email)
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.error = getString(R.string.error_invalid_email)
                }

                //password field
                if (password.isEmpty()) {
                    passwordEditText.error = getString(R.string.error_fill_password)
                } else if (password.length < 8 || !password.matches(passwordPattern)) {
                    passwordEditText.error = getString(R.string.error_password_rules)
                }

                //address field
                if (address.isEmpty()) {
                    addressEditText.error = getString(R.string.error_fill_address)
                }

                if (dob.isEmpty()) {
                    dobEditText.error = getString(R.string.error_fill_dob)
                }
            }
        }

        //for the DatePicker
        dobEditText.setOnClickListener {
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    val formattedDate =
                        "$selectedDay/${selectedMonth + 1}/$selectedYear"

                    dobEditText.setText(formattedDate)

                },
                year, month, day
            )

            datePickerDialog.show()
        }

        // Handle "Already have an account? Login" link click
        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}