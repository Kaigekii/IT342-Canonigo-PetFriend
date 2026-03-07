package com.example.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.mobile.network.ApiClient
import com.example.mobile.network.LoginRequest
import com.example.mobile.util.PreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : Activity() {
    
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView
    
    private lateinit var prefsManager: PreferencesManager
    
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
        
        // Validation
        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            return
        }
        
        val request = LoginRequest(email = email, password = password)
        
        // Disable button during login
        btnLogin.isEnabled = false
        
        ApiClient.apiService.login(request).enqueue(object : Callback<com.example.mobile.network.AuthResponse> {
            override fun onResponse(call: Call<com.example.mobile.network.AuthResponse>, response: Response<com.example.mobile.network.AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    prefsManager.saveToken(authResponse.token)
                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToDashboard()
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    btnLogin.isEnabled = true
                }
            }

            override fun onFailure(call: Call<com.example.mobile.network.AuthResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                btnLogin.isEnabled = true
            }
        })
    }
    
    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
