package iss.nus.edu.sg.mygo.sessions

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject

class SessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun isLoggedIn(): Boolean {
        val token = getToken()
        return token != null && !isTokenExpired(token)
    }

    fun logout() {
        sharedPreferences.edit().remove("auth_token").apply()
    }

    fun getUserIdFromPrefs(): String? {
        return sharedPreferences.getString("user_id", null)
    }


    private fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return true

            val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
            val jsonObject = JSONObject(payload)
            val exp = jsonObject.optLong("exp",0) * 1000 // if exp doesnt exist return 0

            exp ==0L || exp < System.currentTimeMillis() // 当前时间大于过期时间
        } catch (e: Exception) {
            true // 如果解析失败，则认为 Token 过期
        }
    }
}
