package iss.nus.edu.sg.mygo.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.sessions.SessionManager

class MyGoGreeting : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private lateinit var sessionManager: SessionManager
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mygo_greeting)

        welcomeTextView = findViewById(R.id.welcome)

        // 获取 SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        sessionManager = SessionManager(this)

        // **清除登录相关信息**
        clearLoginState()

        // 1.5 秒后跳转到 `MainActivity`
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // **结束当前 Activity，避免返回**
        }, 1500)
    }

    // **清除用户的登录状态**
    private fun clearLoginState() {
        sessionManager.logout() // ✅ 清除 `auth_token`

        // ✅ 只移除 `is_logged_in`，不影响 `RememberMe` 相关数据
        sharedPreferences.edit().remove("is_logged_in").apply()
    }
}
