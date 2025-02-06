package iss.nus.edu.sg.mygo.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.mygo.databinding.LoginActivityBinding

class LoginActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: LoginActivityBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Initialize the binding
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Handle login button click
        binding.loginActionButton.setOnClickListener {
            val username = binding.usernameField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()
            val rememberMeChecked = binding.rememberMeCheckBox.isChecked

            // Validate Inputs
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Get saved user data from SharedPreferences
                val savedUsername = sharedPreferences.getString("Username", null)
                val savedPassword = sharedPreferences.getString("Password", null)

                // Check if the entered username and password match the saved ones
                if (username == savedUsername && password == savedPassword) {
                    // Login Successful
                    if (rememberMeChecked) {
                        // Save "Remember Me" state in SharedPreferences
                        saveRememberMeState(username)
                    }

                    saveLoginState() // Save login state

                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    // Navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // End LoginActivity to prevent returning to it
                } else {
                    // Login Failed
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Handle Forgot Password Click
        binding.forgetPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked!", Toast.LENGTH_SHORT).show()
            // Implement Forgot Password logic here
        }

        // Handle Register Button Click
        binding.registerToggle.setOnClickListener {
            // Navigate to Register Activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Save "Remember Me" State in SharedPreferences
    private fun saveRememberMeState(username: String) {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("SavedUsername", username)
        editor.putBoolean("RememberMe", true)
        editor.apply()
    }

    // Save login state in SharedPreferences
    private fun saveLoginState() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }

    // Load "Remember Me" State from SharedPreferences
    private fun loadRememberMeState() {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val rememberedUsername = sharedPreferences.getString("SavedUsername", "")
        val isRemembered = sharedPreferences.getBoolean("RememberMe", false)

        if (isRemembered) {
            binding.usernameField.setText(rememberedUsername)
            binding.rememberMeCheckBox.isChecked = true
        }
    }

    override fun onStart() {
        super.onStart()
        loadRememberMeState()
    }
}
