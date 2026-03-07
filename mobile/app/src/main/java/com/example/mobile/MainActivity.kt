package com.example.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.mobile.util.PreferencesManager

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val prefsManager = PreferencesManager(this)
        val token = prefsManager.getToken()
        
        // Check if user is already logged in
        if (token != null) {
            // User is logged in, go to Dashboard
            startActivity(Intent(this, DashboardActivity::class.java))
        } else {
            // User is not logged in, go to Splash screen
            startActivity(Intent(this, SplashActivity::class.java))
        }
        
        finish()
    }
}
