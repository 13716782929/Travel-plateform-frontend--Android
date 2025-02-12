package iss.nus.edu.sg.mygo.home

/*
Author: Siti Alifah Binte Yahya
StudentID: A0295324B
Date: 29 Jan 2025
Version: 2.0
*/

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import iss.nus.edu.sg.mygo.api.service.UserApiService
import iss.nus.edu.sg.mygo.api.models.RegisterRequest
import iss.nus.edu.sg.mygo.databinding.RegisterActivityBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: RegisterActivityBinding
    private lateinit var userApiService: UserApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userApiService = UserApiService.create()

        binding.registerButton.setOnClickListener {
            val username = binding.usernameField.text.toString().trim()
            val email = binding.emailField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()
            val confirmPassword = binding.confirmPasswordField.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(username, email, password)
            }
        }


        binding.loginToggle.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = userApiService.register(RegisterRequest(email, password, null))
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RegisterError", "Registration Failed: $errorBody")
                    Toast.makeText(this@RegisterActivity, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("RegisterError", "Network Error: ${e.message}")
                Toast.makeText(this@RegisterActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
