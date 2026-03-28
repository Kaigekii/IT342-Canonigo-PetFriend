package com.example.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.mobile.network.LoginRequest
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginActivity : Activity() {
    
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView
    private lateinit var tvError: TextView
    
    private lateinit var prefsManager: PreferencesManager

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        prefsManager = PreferencesManager(this)
        
        // Check if user is already logged in
        val token = prefsManager.getToken()
        if (token != null) {
            navigateToDashboard()
            return
        }
        
        initViews()
        setupListeners()
    }
    
    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)
        tvError = findViewById(R.id.tvError)
    }
    
    private fun setupListeners() {
        btnLogin.setOnClickListener {
            performLogin()
        }
        
        tvGoToRegister.setOnClickListener {
            navigateToRegister()
        }
    }
    
    private fun navigateToRegister() {
        startActivity(Intent(this, RoleSelectionActivity::class.java))
    }
    
    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        clearError()
        
        // Validation
        if (email.isEmpty()) {
            showError("Email is required")
            return
        }
        
        if (password.isEmpty()) {
            showError("Password is required")
            return
        }
        
        val request = LoginRequest(email = email, password = password)
        
        // Disable button during login
        btnLogin.isEnabled = false

        scope.launch {
            try {
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!

                    prefsManager.saveToken(authResponse.token)
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

                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToDashboard()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = when (response.code()) {
                        401 -> "Invalid email or password"
                        else -> errorBody?.takeIf { it.isNotBlank() } ?: "Login failed"
                    }
                    showError(message)
                    btnLogin.isEnabled = true
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
                btnLogin.isEnabled = true
            }
        }
    }
    
    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun clearError() {
        tvError.text = ""
        tvError.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
