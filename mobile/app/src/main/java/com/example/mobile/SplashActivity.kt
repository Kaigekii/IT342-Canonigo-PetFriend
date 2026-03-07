package com.example.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.mobile.util.PreferencesManager

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Check if user is already logged in
        val prefsManager = PreferencesManager(this)
        if (prefsManager.isLoggedIn()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        // Setup button click listeners
        findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            startActivity(Intent(this, RoleSelectionActivity::class.java))
        }
    }
}
