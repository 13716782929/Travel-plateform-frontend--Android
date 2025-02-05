package iss.nus.edu.sg.mygo.home

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.fragment.HomeFragment
import iss.nus.edu.sg.mygo.fragment.NotificationFragment
import iss.nus.edu.sg.mygo.fragment.ProfileFragment
import iss.nus.edu.sg.mygo.fragment.SearchFragment

class Main1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_activity)
        enableEdgeToEdge() // 确保此行代码执行
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
                    NotificationFragment()
                }
                R.id.nav_profile ->{
                    ProfileFragment()
                }
                else -> HomeFragment() // 默认Fragment
            }
            loadFragment(fragment) // 替换Fragment
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // 替换container中的Fragment
            .commit()
    }
}
