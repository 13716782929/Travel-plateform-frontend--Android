package iss.nus.edu.sg.mygo.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.fragment.HomeFragment
import iss.nus.edu.sg.mygo.fragment.NotificationFragment
import iss.nus.edu.sg.mygo.fragment.ProfileFragment
import iss.nus.edu.sg.mygo.fragment.SearchFragment
import iss.nus.edu.sg.mygo.fragment.ScheduleFragment
import iss.nus.edu.sg.mygo.models.FlightTicketViewModel

/**
 * @ClassName MainActivity
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/1/25
 * @Version 1.3
 */


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_activity)
        enableEdgeToEdge()

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE) // 获取SharedPreferences


        if (savedInstanceState == null) {
            loadFragment(HomeFragment()) // 默认加载HomeFragment
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        setupBottomNavigationView(bottomNavigationView)
    }

    private fun setupBottomNavigationView(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val fragment: Fragment = when (menuItem.itemId) {
                R.id.nav_home -> {
                    HomeFragment() // 切换到HomeFragment
                }
                R.id.nav_search -> {
                    SearchFragment() // 切换到SearchFragment
                }
                R.id.nav_notifications -> {
                    NotificationFragment() // 切换到NotificationFragment
                }
                R.id.nav_profile -> {
                    ProfileFragment() // 如果已登录，加载ProfileFragment
                }
                R.id.nav_calendar -> {
                    ScheduleFragment()
                }
                else -> HomeFragment() // 默认Fragment
            }
            loadFragment(fragment) // 替换Fragment
            true
        }
    }

    // 检查用户是否已登录
    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    // 跳转到 LoginActivity
    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // 替换container中的Fragment
            .commit()
    }

}
