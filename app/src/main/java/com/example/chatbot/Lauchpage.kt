package com.example.chatbot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Lauchpage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lauchpage)
    }

    // Function to handle login button click
    fun onLoginButtonClick(view: View) {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    // Function to handle sign up button click
    fun onSignUpButtonClick(view: View) {
        val intent = Intent(this, Register::class.java)  // Replace SignUp::class.java with your actual sign-up activity class
        startActivity(intent)
    }
}
