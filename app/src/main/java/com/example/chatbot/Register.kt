package com.example.chatbot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatbot.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore // Added Firestore instance
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance() // Initialize firebaseAuth
        firestore = FirebaseFirestore.getInstance() // Initialize Firestore

        // Set password input type to be hidden
        binding.password.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
        binding.password2.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()

        binding.passwordLayout.setOnClickListener {
            togglePasswordVisibility(binding.password)
        }

        binding.password2Layout.setOnClickListener {
            togglePasswordVisibility(binding.password2)
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.fullName.text.toString()
            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()
            val confirmPass = binding.password2.text.toString()
            val dob = binding.dob.text.toString()

            // Reset errors
            resetErrors()

            if (name.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && dob.isNotEmpty()) {
                if (isValidEmail(email)) {
                    if (pass == confirmPass) {
                        if (isValidDOB(dob)) {
                            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Send email verification
                                        val user = firebaseAuth.currentUser
                                        user?.sendEmailVerification()
                                            ?.addOnCompleteListener { verificationTask ->
                                                if (verificationTask.isSuccessful) {
                                                    // Add user data to Firestore
                                                    val userData = hashMapOf(
                                                        "name" to name,
                                                        "email" to email,
                                                        "dob" to dob
                                                    )
                                                    firestore.collection("users")
                                                        .document(user.uid)
                                                        .set(userData)
                                                        .addOnSuccessListener {
                                                            // Show success message
                                                            Toast.makeText(this, "Sign up successful! Verification email sent.", Toast.LENGTH_SHORT).show()

                                                            // Move to the login screen or any other appropriate action
                                                            val intent = Intent(this, Login::class.java)
                                                            startActivity(intent)
                                                        }
                                                        .addOnFailureListener { e ->
                                                            // Show error message if adding data to Firestore fails
                                                            Toast.makeText(this, "Failed to add user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                                        }
                                                } else {
                                                    Toast.makeText(this, verificationTask.exception?.message, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            showError(binding.dobLayout, "Invalid date of birth")
                        }
                    } else {
                        showError(binding.password2Layout, "Passwords don't match")
                    }
                } else {
                    showError(binding.emailLayout, "Invalid email format")
                }
            } else {
                // Display a Toast message for the error
                Toast.makeText(this, "Empty fields not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return Pattern.matches(emailPattern, email)
    }

    private fun isValidDOB(dob: String): Boolean {
        // Assuming dob is in the format "dd/MM/yyyy"
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false

        try {
            val dobDate = sdf.parse(dob)
            val currentDate = Calendar.getInstance().time

            // Check if dobDate is before the current date
            return dobDate != null && dobDate.before(currentDate)
        } catch (e: Exception) {
            return false
        }
    }

    private fun showError(textInputLayout: com.google.android.material.textfield.TextInputLayout, errorMessage: String) {
        textInputLayout.error = errorMessage
        textInputLayout.setErrorTextColor(resources.getColorStateList(R.color.button_color)) // set color
        textInputLayout.boxStrokeColor = resources.getColor(R.color.button_color)
    }

    private fun resetErrors() {
        binding.fullNameLayout.error = null
        binding.emailLayout.error = null
        binding.passwordLayout.error = null
        binding.password2Layout.error = null
        binding.dobLayout.error = null

        // Reset box stroke colors
        binding.fullNameLayout.boxStrokeColor = resources.getColor(R.color.black)
        binding.emailLayout.boxStrokeColor = resources.getColor(R.color.black)
        binding.passwordLayout.boxStrokeColor = resources.getColor(R.color.black)
        binding.password2Layout.boxStrokeColor = resources.getColor(R.color.black)
        binding.dobLayout.boxStrokeColor = resources.getColor(R.color.black)
    }

    // This method is called when the "Have an account? Login" text is clicked
    fun onLoginTextClick(view: View) {
        // Handle the click event, navigate to the login activity
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    private fun togglePasswordVisibility(textInputEditText: com.google.android.material.textfield.TextInputEditText) {
        if (!isPasswordVisible) {
            // Show password
            textInputEditText.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
        } else {
            // Hide password
            textInputEditText.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
        }
        isPasswordVisible = isPasswordVisible

        // Move cursor to the end of the text
        textInputEditText.text?.let { textInputEditText.setSelection(it.length) }
    }
}