package iss.nus.edu.sg.mygo.sessions

/**
 * Class Name: SessionBaseActivity
 * func: Activities can inherit this class to set session check
 *      if user doesn't login then Activities will be redirected to LoginActivity
 */

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.mygo.home.LoginActivity
import iss.nus.edu.sg.mygo.sessions.SessionManager // IDE自动解析同个包，无需import

open class SessionBaseActivity :AppCompatActivity() {

    private  lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        if(!sessionManager.isLoggedIn()){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

/**
 * using Example
 * class NeedSessionActivity : BaseActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         // 这里不需要再检查登录状态，BaseActivity 已经处理了
 *     }
 * }
 */