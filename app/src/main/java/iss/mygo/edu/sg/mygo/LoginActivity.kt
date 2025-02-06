package iss.mygo.edu.sg.mygo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nus.registernewuser.databinding.LoginBinding

class LoginActivity : AppCompatActivity() {

    //viewBinding
    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //initialise binding
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //handle login button click
        binding.loginActionButton.setOnClickListener {
            val username = binding.usernameField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()
            val rememberMeChecked = binding.rememberMeCheckBox.isChecked

            // Validate Inputs
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (username == "admin" && password == "1234") {
                // Login Successful
                if (rememberMeChecked) {
                    // Save "Remember Me" state in SharedPreferences
                    saveRememberMeState(username)
                }

                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                // Navigate to another activity (e.g., dashboard)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
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
    }

    // Save "Remember Me" State in SharedPreferences
    private fun saveRememberMeState(username: String) {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("SavedUsername", username)
        editor.putBoolean("RememberMe", true)
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

