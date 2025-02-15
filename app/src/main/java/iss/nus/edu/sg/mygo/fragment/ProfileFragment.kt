package iss.nus.edu.sg.mygo.fragment

/*
Class Name: ProfileFragment
Author: Siti Alifah Binte Yahya and Wang Chang and Yao Yiyang
StudentID: A0295324B and A0310544R and A0294873L
Date: 10 Feb 2025
Version: 2.0
*/

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.api.models.Preference
import iss.nus.edu.sg.mygo.api.service.UserApiService
import iss.nus.edu.sg.mygo.home.LoginActivity
import iss.nus.edu.sg.mygo.network.RetrofitClient
import iss.nus.edu.sg.mygo.sessions.SessionManager
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private val apiService = UserApiService.create()

    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var logoutButton: ImageView

    private lateinit var travelTypeSpinner: Spinner
    private lateinit var budgetRangeEditText: EditText
    private lateinit var languageSpinner: Spinner
    private lateinit var saveButton: Button

    private lateinit var accountNumberTextView: TextView
    private lateinit var phoneNumberTextView: TextView
    private lateinit var realNameTextView: TextView

    private var userId: Int = -1

     // 🔥 绑定 Logout 按钮

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ** 正确初始化 `SessionManager`**
        sessionManager = SessionManager(requireContext())
        val isLoggedIn = sessionManager.isLoggedIn()
        showToast("🔍 DEBUG: isLoggedIn = $isLoggedIn") // ✅ 打印登录状态

        if (!isLoggedIn) {
            showToast("🔍 DEBUG: User not logged in, redirecting to LoginActivity")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            return null
        }

        // ** 这里要用 `val view` 保存 Layout，否则 `setOnClickListener` 失效**
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        // ** 绑定 UI 组件**
        usernameTextView = view.findViewById(R.id.profile_username_txt)
        emailTextView = view.findViewById(R.id.profile_email_txt)
        logoutButton = view.findViewById(R.id.logOut_icon)

        travelTypeSpinner = view.findViewById(R.id.travel_type_spinner)
        budgetRangeEditText = view.findViewById(R.id.budget_range_edit_text)
        languageSpinner = view.findViewById(R.id.language_spinner)
        saveButton = view.findViewById(R.id.save_preferences_button)

//        // 设置返回按钮的点击事件
//        val backButton: ImageButton = view.findViewById(R.id.button_back)
//        backButton.setOnClickListener { finish() }

        logoutButton.setOnClickListener { logoutUser() }
        saveButton.setOnClickListener { updateUserPreferences() }

        // ✅ 设置 "旅行类型" 下拉框选项
        val travelTypes = listOf("Single", "Couple", "Family")
        val travelTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, travelTypes)
        travelTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        travelTypeSpinner.adapter = travelTypeAdapter

        // ✅ 设置 "语言偏好" 下拉框选项
        val languages = listOf("English", "Chinese", "French")
        val languageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = languageAdapter



        // ** 确保用户登录状态**
        val userId = sessionManager.getUserIdFromPrefs()
        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
        } else {
            this.userId = userId.toInt() // ✅ 确保 userId 被赋值
            fetchUserProfile()
        }

        // ** 监听 Logout 按钮**
        logoutButton.setOnClickListener {
            logoutUser()
        }

        return view // ** 这里要返回 `view`**
    }

    /**
     * 处理 Logout 逻辑
     */
    private fun logoutUser() {
        // **清空 Session**
        sessionManager.logout()

        Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()

        // ** 跳转到 LoginActivity**
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }


    private fun fetchUserProfile() {
        lifecycleScope.launch {
            try {
                val response = apiService.getUserProfile(userId)
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        usernameTextView.text = user.userId.toString()
                        emailTextView.text = user.email
                        user.preference?.let { loadPreferences(it) }
                    }
                } else {
                    showToast("Failed to load profile")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun loadPreferences(preference: Preference) {
        travelTypeSpinner.setSelection(
            resources.getStringArray(R.array.travel_types).indexOf(preference.travelType)
        )
        budgetRangeEditText.setText(preference.budgetRange.toString())
        languageSpinner.setSelection(
            resources.getStringArray(R.array.languages).indexOf(preference.language)
        )
    }

    private fun updateUserPreferences() {
        val budgetText = budgetRangeEditText.text.toString().trim()
        val budgetRange = try {
            if (budgetText.isEmpty()) BigDecimal("0.00") else BigDecimal(budgetText)
        } catch (e: NumberFormatException) {
            showToast("Invalid budget format, please enter a valid number")
            return
        }

        val updatedPreferences = Preference(
            travelType = travelTypeSpinner.selectedItem.toString(),
            budgetRange = budgetRange,
            language = languageSpinner.selectedItem.toString()
        )

        lifecycleScope.launch {
            try {
                val response = apiService.updateUserPreferences(userId, updatedPreferences)
                if (response.isSuccessful) {
                    showToast("Preferences updated successfully!")
                } else {
                    showToast("Failed to update preferences")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
