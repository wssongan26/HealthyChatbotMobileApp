package com.example.chatbot

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatbot.databinding.ActivityForgotpasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class Forgot : AppCompatActivity() {

    private lateinit var binding: ActivityForgotpasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root) // Use binding.root to set the content view

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
    }


    // Handle click on the top image to go back to the login page
    fun onBackPressed(view: View) {
        finish() // Close the Forgot activity and go back to the previous activity (login)
    }

    // Handle click on the "Send" button
    // Handle click on the "Send" button
    fun onSendButtonClick(view: View) {
        val email = binding.email.text.toString().trim()

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Email is valid, initiate password reset
        sendPasswordResetEmail(email)
    }

    // Function to check if the entered email is in a valid format
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    // Function to send a password reset email
    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { resetTask ->
                if (resetTask.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Password reset email sent to $email",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // Optionally, you can finish the activity after sending the reset email
                } else {
                    Toast.makeText(
                        this,
                        "Error sending password reset email: ${resetTask.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
