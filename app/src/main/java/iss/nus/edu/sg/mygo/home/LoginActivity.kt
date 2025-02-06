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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Initialize the binding
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle window insets for edge-to-edge layout
        // Commenting out this part for now to avoid the NullPointerException
        // ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
        //     val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        //     v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        //     insets
        // }

        // Handle login button click
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
                // Navigate to another activity
                // You can implement navigation here if required
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
