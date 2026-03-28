package com.example.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.mobile.network.RegisterRequest
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegisterActivity : Activity() {
    
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    
    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etAge: TextInputEditText
    private lateinit var etGender: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView
    
    private lateinit var prefsManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        prefsManager = PreferencesManager(this)
        
        // Check if user is already logged in
        if (prefsManager.isLoggedIn()) {
            navigateToDashboard()
            return
        }
        
        initViews()
        setupListeners()
    }
    
    private fun initViews() {
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etAge = findViewById(R.id.etAge)
        etGender = findViewById(R.id.etGender)
        etAddress = findViewById(R.id.etAddress)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)
    }
    
    private fun setupListeners() {
        btnRegister.setOnClickListener {
            performRegister()
        }
        
        tvGoToLogin.setOnClickListener {
            navigateToLogin()
        }
    }
    
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
    
    override fun onBackPressed() {
        navigateToLogin()
    }
    
    private fun performRegister() {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val ageStr = etAge.text.toString().trim()
        val gender = etGender.text.toString().trim()
        val address = etAddress.text.toString().trim()
        
        // Validation
        if (firstName.isEmpty()) {
            Toast.makeText(this, "First name is required", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (lastName.isEmpty()) {
            Toast.makeText(this, "Last name is required", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidPassword(password)) {
            Toast.makeText(
                this,
                "Password must be at least 8 characters with 1 uppercase, 1 number, and 1 special character",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val request = RegisterRequest(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            phoneNumber = null,
            address = address.ifEmpty { null },
            role = "PET_OWNER"
        )
        
        // Disable button during registration
        btnRegister.isEnabled = false
        
        scope.launch {
            try {
                val response = RetrofitClient.apiService.register(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    
                    // Save token and user data
                    prefsManager.saveAuthToken(authResponse.token)
                    prefsManager.saveUserData(
                        userId = authResponse.userId,
                        firstName = authResponse.firstName,
                        lastName = authResponse.lastName,
                        email = authResponse.email,
                        phoneNumber = authResponse.phoneNumber,
                        address = authResponse.address,
                        role = authResponse.role,
                        isVerified = authResponse.isVerified
                    )
                    
                    Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                    navigateToDashboard()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Registration failed"
                    Toast.makeText(this@RegisterActivity, errorBody, Toast.LENGTH_LONG).show()
                    btnRegister.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                btnRegister.isEnabled = true
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isDigit() } &&
                password.any { "!@#\$%^&*(),.?\":{}|<>".contains(it) }
    }
    
    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
