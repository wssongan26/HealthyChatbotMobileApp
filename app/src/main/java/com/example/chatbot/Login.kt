package com.example.chatbot

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatbot.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnTogglePasswordVisibility.setOnClickListener {
            // Toggle password visibility
            isPasswordVisible = !isPasswordVisible
            updatePasswordVisibility()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Sign in with email and password
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Check if email is verified before allowing login
                            val user = firebaseAuth.currentUser
                            if (user != null && user.isEmailVerified) {
                                // Login successful and email is verified, navigate to MainActivity
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish() // Close the login activity to prevent the user from coming back
                            } else {
                                // Email is not verified, show a message
                                Toast.makeText(
                                    this,
                                    "Please verify your email before logging in",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Authentication failed
                            Toast.makeText(
                                this,
                                "Authentication failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                // Email or password is empty
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // This method is called when the "Sign Up" text is clicked
    fun onSignUpClick(view: View) {
        // Handle the click event, navigate to the register activity
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }

    // This method is called when the "Reset Password" text is clicked
    fun onResetPasswordClick(view: View) {
        // Handle the click event, navigate to the Forgot activity
        val intent = Intent(this, Forgot::class.java)
        startActivity(intent)
    }

    private fun updatePasswordVisibility() {
        // Update the password visibility based on the current state
        val visibility = if (isPasswordVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.password.transformationMethod =
            if (isPasswordVisible) null else PasswordTransformationMethod.getInstance()
        binding.btnTogglePasswordVisibility.setImageResource(
            if (isPasswordVisible) R.drawable.baseline_remove_red_eye_24
            else R.drawable.baseline_remove_red_eye_24
        )
    }
}
