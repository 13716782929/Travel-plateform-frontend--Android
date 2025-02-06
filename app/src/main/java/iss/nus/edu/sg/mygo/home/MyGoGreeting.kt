package iss.nus.edu.sg.mygo.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.mygo.R

class MyGoGreeting : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView // 用于引用 TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mygo_greeting)

        welcomeTextView = findViewById(R.id.welcome)

        // 使用 Handler 与 Looper.getMainLooper() 来延迟 3 秒后跳转到主页面
        Handler(Looper.getMainLooper()).postDelayed({
            // 创建一个 Intent 跳转到主页（假设主页面为 MainActivity）
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // 结束当前 Activity，避免返回到该页面
            finish()
        }, 3000) // 3000 毫秒 = 3 秒
    }
}
