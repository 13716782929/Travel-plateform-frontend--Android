package iss.nus.edu.sg.mygo.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.mygo.databinding.RegisterActivityBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: RegisterActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding
        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Access views through binding
        val registerButton = binding.registerButton

        // Handle Register Button Click
        registerButton.setOnClickListener {
            val username = binding.usernameField.text.toString().trim()
            val email = binding.emailField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()
            val confirmPassword = binding.confirmPasswordField.text.toString().trim()

            // Validate inputs
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            } else {
                // Save user registration data (for demo purposes, using SharedPreferences)
                saveUserData(username, email, password)

                // Show success message
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                // Navigate to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

                // Optionally, finish the current activity to prevent going back to RegisterActivity
                finish()
            }
        }

        // Handle Login Toggle Button Click (to navigate back to LoginActivity)
        binding.loginToggle.setOnClickListener {
            // Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Optionally, finish the current activity
            finish()
        }
    }

    // Save user data in SharedPreferences
    private fun saveUserData(username: String, email: String, password: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("Username", username)
        editor.putString("Email", email)
        editor.putString("Password", password) // In a real app, don't store passwords in plain text!
        editor.apply()
    }
}
