package com.example.aukcje20

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the DrawerLayout
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        // Find the NavigationView
        val navView: NavigationView = findViewById(R.id.nav_view)



        // Set up the navigation drawer menu
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    // Handle Home
                    true
                }
                R.id.nav_profile -> {
                    // Handle Profile
                    true
                }
                R.id.nav_settings -> {
                    // Handle Settings
                    true
                }
                else -> false
            }
        }

        // Set up the drawer toggle
        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onSupportNavigateUp(): Boolean {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        return if (drawerLayout.isDrawerOpen(findViewById(R.id.nav_view))) {
            drawerLayout.closeDrawer(findViewById(R.id.nav_view))
            true
        } else {
            super.onSupportNavigateUp()
        }
    }
}