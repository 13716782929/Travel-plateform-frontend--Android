package iss.nus.edu.sg.mygo.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.mygo.databinding.LoginActivityBinding

class LoginActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: LoginActivityBinding

    // 模拟的用户数据
    private val fakeUsers = mapOf(
        "admin" to "1234",
        "user1" to "password1",
        "user2" to "password2"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Initialize the binding
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle login button click
        binding.loginActionButton.setOnClickListener {
            val username = binding.usernameField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()
            val rememberMeChecked = binding.rememberMeCheckBox.isChecked

            // Validate Inputs
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (fakeUsers[username] == password) {
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
