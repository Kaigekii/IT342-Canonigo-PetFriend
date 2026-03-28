package com.example.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashboardActivity : Activity() {
    
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvAddress: TextView
    private lateinit var llPhone: LinearLayout
    private lateinit var llRole: LinearLayout
    private lateinit var llAddress: LinearLayout
    private lateinit var btnLogout: Button
    
    private lateinit var prefsManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        
        prefsManager = PreferencesManager(this)
        
        // Check if user is logged in
        if (!prefsManager.isLoggedIn()) {
            navigateToLogin()
            return
        }
        
        initViews()
        setupListeners()
        loadUserProfile()
    }
    
    private fun initViews() {
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvRole = findViewById(R.id.tvRole)
        tvAddress = findViewById(R.id.tvAddress)
        llPhone = findViewById(R.id.llPhone)
        llRole = findViewById(R.id.llRole)
        llAddress = findViewById(R.id.llAddress)
        btnLogout = findViewById(R.id.btnLogout)
    }
    
    private fun setupListeners() {
        btnLogout.setOnClickListener {
            performLogout()
        }
    }
    
    private fun loadUserProfile() {
        // First, display cached data
        displayCachedUserData()
        
        // Then fetch fresh data from server
        val token = prefsManager.getAuthToken()
        if (token != null) {
            scope.launch {
                try {
                    val response = RetrofitClient.apiService.getCurrentUser("Bearer $token")
                    
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!
                        
                        // Update cached data
                        prefsManager.saveUserData(
                            userId = user.userId,
                            firstName = user.firstName,
                            lastName = user.lastName,
                            email = user.email,
                            phoneNumber = user.phoneNumber,
                            address = user.address,
                            role = user.role,
                            isVerified = user.isVerified
                        )
                        
                        // Display fresh data
                        displayUserData(
                            user.firstName,
                            user.lastName,
                            user.email,
                            user.phoneNumber,
                            user.role,
                            user.address
                        )
                    } else if (response.code() == 401) {
                        // Token expired or invalid
                        Toast.makeText(this@DashboardActivity, "Session expired. Please login again.", Toast.LENGTH_LONG).show()
                        performLogout()
                    }
                } catch (e: Exception) {
                    // If network fails, we already have cached data displayed
                    Toast.makeText(this@DashboardActivity, "Using cached profile data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun displayCachedUserData() {
        val firstName = prefsManager.getFirstName() ?: ""
        val lastName = prefsManager.getLastName() ?: ""
        val email = prefsManager.getEmail() ?: ""
        val phoneNumber = prefsManager.getPhoneNumber()
        val role = prefsManager.getRole()
        val address = prefsManager.getAddress()
        
        displayUserData(firstName, lastName, email, phoneNumber, role, address)
    }
    
    private fun displayUserData(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String?,
        role: String?,
        address: String?
    ) {
        tvName.text = "$firstName $lastName"
        tvEmail.text = email
        
        if (!phoneNumber.isNullOrEmpty()) {
            tvPhone.text = phoneNumber
            llPhone.visibility = View.VISIBLE
        } else {
            llPhone.visibility = View.GONE
        }
        
        if (!role.isNullOrEmpty()) {
            tvRole.text = role
            llRole.visibility = View.VISIBLE
        } else {
            llRole.visibility = View.GONE
        }
        
        if (!address.isNullOrEmpty()) {
            tvAddress.text = address
            llAddress.visibility = View.VISIBLE
        } else {
            llAddress.visibility = View.GONE
        }
    }
    
    private fun performLogout() {
        prefsManager.clearAll()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        navigateToLogin()
    }
    
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
    
    override fun onBackPressed() {
        // On dashboard, back button exits the app (don't go back to login)
        finishAffinity()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
