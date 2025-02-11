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

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val apiService = UserApiService.create() //  初始化 API 服务
    private val sessionManager by lazy { SessionManager(this) } // using SessionManager doing authentication


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // 监听登录按钮点击事件
        binding.loginActionButton.setOnClickListener {
            val email = binding.usernameField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()
            val rememberMeChecked = binding.rememberMeCheckBox.isChecked

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password, rememberMeChecked) // 调用 API 进行身份验证
            }
        }

        // 忘记密码点击事件
        binding.forgetPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked!", Toast.LENGTH_SHORT).show()
            // 实现忘记密码功能
        }

        // 注册按钮点击事件
        binding.registerToggle.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * 通过 API 进行登录，并存储 Token
     * login by API and store Token
     */
    private fun loginUser(email: String, password: String, rememberMe: Boolean) {
        lifecycleScope.launch {
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        saveToken(it.token, it.userId) // **存 JWT Token & userId**
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
     * 存储 JWT Token 以便后续 API 请求使用
     * store JWT Token for API usage
     */
    private fun saveToken(token: String, userId: String) {
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token) // 存 JWT Token
        editor.putString("user_id", userId) // 存 userId
        editor.apply()
    }


    /**
     * 记住用户（不存储密码，只存储 email）
     * remember user with email
     */
    private fun saveRememberMeState(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("SavedEmail", email)
        editor.putBoolean("RememberMe", true)
        editor.apply()
    }

    /**
     * 记录用户已登录状态
     * record user login status
     */
    private fun saveLoginState() {
        sharedPreferences.edit().putBoolean("is_logged_in", true).apply()
    }

    /**
     * 加载 "Remember Me" 状态
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
