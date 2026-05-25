package com.example.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.mobile.util.PreferencesManager

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Check if user is already logged in
        val prefsManager = PreferencesManager(this)
        if (prefsManager.isLoggedIn()) {
            val role = prefsManager.getRole()
            if (role == "PET_OWNER") {
                startActivity(Intent(this, com.example.mobile.features.dashboard.OwnerMainActivity::class.java))
            } else if (role == "PET_SITTER") {
                startActivity(Intent(this, com.example.mobile.features.dashboard.SitterMainActivity::class.java))
            } else {
                prefsManager.clearAll()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
            return
        }

        // Setup button click listeners
        findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            startActivity(Intent(this, RoleSelectionActivity::class.java))
        }

        findViewById<TextView>(R.id.tvSignIn).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
