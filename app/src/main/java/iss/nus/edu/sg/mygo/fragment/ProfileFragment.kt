package iss.nus.edu.sg.mygo.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.api.service.UserApiService
import iss.nus.edu.sg.mygo.home.LoginActivity
import iss.nus.edu.sg.mygo.sessions.SessionManager

class ProfileFragment : Fragment() {

    private val apiService = UserApiService.
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sessionManager = SessionManager(requireContext())

        val isLoggedIn = sessionManager.isLoggedIn()
        println("🔍 DEBUG: isLoggedIn = $isLoggedIn") // ✅ 打印登录状态

        if (!isLoggedIn) {
            println("🔍 DEBUG: User not logged in, redirecting to LoginActivity")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            return null
        }

        return inflater.inflate(R.layout.profile_fragment, container, false)
    }
}
