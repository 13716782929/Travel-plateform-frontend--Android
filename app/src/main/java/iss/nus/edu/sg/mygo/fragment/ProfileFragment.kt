package iss.nus.edu.sg.mygo.fragment

/*
Class name:
Author: Siti Alifah Binte Yahya
StudentID: A0295324B
Date: 11 Feb 2025
Version: 2.0
*/


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.api.service.UserApiService
import iss.nus.edu.sg.mygo.home.LoginActivity
import iss.nus.edu.sg.mygo.models.User
import iss.nus.edu.sg.mygo.network.RetrofitClient
import iss.nus.edu.sg.mygo.sessions.SessionManager
import kotlinx.coroutines.launch
import okhttp3.Callback
import retrofit2.Call
import retrofit2.Response
import java.lang.reflect.UndeclaredThrowableException

class ProfileFragment : Fragment() {

    // Initialize UserApiService properly
    private val apiService = UserApiService
    // Use lateinit properly
    private lateinit var sessionManager: SessionManager // variable stores an instance of the SessionManager
    private lateinit var usernameTextView: TextView // represents the TextView in your XML layout (profile_username_txt), which displays the username retrieved from the backend
    private lateinit var accountNumberTextView: TextView //references the TextView in XML (profile_email_txt), which displays the user's email
    private lateinit var emailTextView: TextView
    private lateinit var phoneNumberTextView: TextView //represents the TextView in XML (profile_phone_txt)
    private lateinit var realNameTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        // Assign value to the lateinit sessionManager
        var sessionManager = SessionManager(requireContext())

        usernameTextView = view.findViewById(R.id.profile_username_txt)
        accountNumberTextView = view.findViewById(R.id.profile_accountNum_txt)
        emailTextView = view.findViewById(R.id.profile_email_txt)
        phoneNumberTextView = view.findViewById(R.id.profile_phoneNum_txt)
        realNameTextView = view.findViewById(R.id.profile_realName_txt)

        val isLoggedIn = sessionManager.isLoggedIn()
        println("🔍 DEBUG: isLoggedIn = $isLoggedIn")

        if (!isLoggedIn) {
            println("🔍 DEBUG: User not logged in, redirecting to LoginActivity")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        } else {
            fetchUserProfile()
        }

        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    private fun fetchUserProfile() {
        val token = sessionManager.getToken() ?: return

        val apiService = RetrofitClient.instance.create(UserApiService:: class.java)

        // ✅ Use coroutine to call suspend function
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val user = apiService.getUserProfile("Bearer $token")
                usernameTextView.text = user.username
                emailTextView.text = user.email
                phoneNumberTextView.text = user.phoneNumber
                realNameTextView.text = user.realName
            } catch (e: Exception) {
                println(" Error fetching user profile: ${e.message}")
            }

        }
    }
}
