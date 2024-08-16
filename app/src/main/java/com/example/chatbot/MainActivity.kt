package com.example.chatbot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.GravityCompat
import com.example.chatbot.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import android.view.MenuItem
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.example.chatbot.Login // Assuming Login is your login activity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())
        firebaseAuth = FirebaseAuth.getInstance()

        // Set OnClickListener for the navigation ImageView
        binding.navigation.setOnClickListener {
            // Open the drawer when the ImageView is clicked
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.BottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.chatbot -> replaceFragment(Chatbot())
                R.id.profile -> replaceFragment(Profile())
                R.id.setting -> replaceFragment(Setting())

                else -> {
                }
            }
            true
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> replaceFragment(Home())
                R.id.nav_chatbot -> replaceFragment(Chatbot())
                R.id.nav_profile -> replaceFragment(Profile())
                R.id.nav_settings -> replaceFragment(Setting()) // Logout option
                R.id.nav_logout -> logout() // Logout option
            }
            // Close the drawer when an item is selected
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun logout() {
        firebaseAuth.signOut()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}
