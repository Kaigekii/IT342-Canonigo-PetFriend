package com.example.mobile.features.dashboard

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.mobile.R
import com.example.mobile.features.bookings.SitterRequestsFragment
import com.example.mobile.features.messages.MessagesFragment
import com.example.mobile.features.sitters.SitterProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class SitterMainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sitter_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_sitter_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SitterHomeFragment())
                        .commit()
                    true
                }
                R.id.nav_sitter_requests -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SitterRequestsFragment())
                        .commit()
                    true
                }
                R.id.nav_sitter_messages -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MessagesFragment())
                        .commit()
                    true
                }
                R.id.nav_sitter_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SitterProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_sitter_home
        }
    }
}
