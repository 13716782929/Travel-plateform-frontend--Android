package iss.nus.edu.sg.mygo.fragment

/*
Class Name: ProfileFragment
Author: Siti Alifah Binte Yahya and Wang Chang
StudentID: A0295324B and A0310544R
Date: 10 Feb 2025
Version: 2.0
*/

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.home.LoginActivity
import iss.nus.edu.sg.mygo.sessions.SessionManager

class ProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var usernameTextView: TextView
    private lateinit var accountNumberTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneNumberTextView: TextView
    private lateinit var realNameTextView: TextView
    private lateinit var logoutButton: ImageView // 🔥 绑定 Logout 按钮

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // ** 这里要用 `val view` 保存 Layout，否则 `setOnClickListener` 失效**
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        // ** 正确初始化 `SessionManager`**
        sessionManager = SessionManager(requireContext())

        // ** 绑定 UI 组件**
        usernameTextView = view.findViewById(R.id.profile_username_txt)
        accountNumberTextView = view.findViewById(R.id.profile_accountNum_txt)
        emailTextView = view.findViewById(R.id.profile_email_txt)
        phoneNumberTextView = view.findViewById(R.id.profile_phoneNum_txt)
        realNameTextView = view.findViewById(R.id.profile_realName_txt)
        logoutButton = view.findViewById(R.id.logOut_icon)

        // ** 确保用户登录状态**
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
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

    /**
     * 跳转到 LoginActivity
     */
    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
