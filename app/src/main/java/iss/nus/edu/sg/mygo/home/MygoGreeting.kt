package iss.nus.edu.sg.mygo.home

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.mygo.R

class MygoGreeting : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView // 用于引用 TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_mygo_greeting)


        welcomeTextView = findViewById(R.id.welcome)

    }
}
