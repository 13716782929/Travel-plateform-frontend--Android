package iss.nus.edu.sg.mygo.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import iss.nus.edu.sg.mygo.api.service.UserApiService
import iss.nus.edu.sg.mygo.api.models.LoginRequest
import iss.nus.edu.sg.mygo.databinding.LoginActivityBinding
import iss.nus.edu.sg.mygo.sessions.SessionManager
import kotlinx.coroutines.launch

/*
Author: Siti Alifah Binte Yahya
StudentID: A0295324B
Date: 28 Jan 2025
Version: 2.0
*/

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val apiService = UserApiService.create()
    private val sessionManager by lazy { SessionManager(this) } // using SessionManager doing authentication


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)


        binding.loginActionButton.setOnClickListener {
            val email = binding.usernameField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()
            val rememberMeChecked = binding.rememberMeCheckBox.isChecked

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password, rememberMeChecked)
            }
        }


        binding.forgetPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked!", Toast.LENGTH_SHORT).show()

        }


        binding.registerToggle.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * login by API and store Token
     */
    private fun loginUser(email: String, password: String, rememberMe: Boolean) {
        lifecycleScope.launch {
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        saveToken(it.token, it.userId) // * JWT Token & userId**
                        if (rememberMe) saveRememberMeState(email)
                        saveLoginState()

                        Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                        // 🔹 判断是否是从别的 Activity 跳转过来的
                        if (intent.hasExtra("from_activity")) {
                            setResult(RESULT_OK) // 让前一个页面知道登录成功了
                            finish() // 直接回到上一个页面
                        } else {
                            // 否则跳转到主页面
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login Failed: Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**

     * store JWT Token for API usage
     */
    private fun saveToken(token: String, userId: String) {
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token) // JWT Token
        editor.putString("user_id", userId) // userId
        editor.apply()
    }


    /**
     * email
     * remember user with email
     */
    private fun saveRememberMeState(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("SavedEmail", email)
        editor.putBoolean("RememberMe", true)
        editor.apply()
    }

    /**
     *
     * record user login status
     */
    private fun saveLoginState() {
        sharedPreferences.edit().putBoolean("is_logged_in", true).apply()
    }

    /**
     * "Remember Me"
     * load Remember Me status
     */
    private fun loadRememberMeState() {
        val rememberedEmail = sharedPreferences.getString("SavedEmail", "")
        val isRemembered = sharedPreferences.getBoolean("RememberMe", false)

        if (isRemembered) {
            binding.usernameField.setText(rememberedEmail)
            binding.rememberMeCheckBox.isChecked = true
        }
    }

    override fun onStart() {
        super.onStart()
        loadRememberMeState()
    }
}
