package iss.nus.edu.sg.mygo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.mygo.databinding.RegisterActivityBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: RegisterActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialise the binding
        binding= RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Access views through binding
        val registerButton = binding.registerButton

        //Handle Register Button Click
        registerButton.setOnClickListener {
            val username = binding.usernameField.text.toString()
            val email = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()
            val confirmPassword = binding.confirmPasswordField.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                //RegistrationLogic (eg save user data to a database
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
