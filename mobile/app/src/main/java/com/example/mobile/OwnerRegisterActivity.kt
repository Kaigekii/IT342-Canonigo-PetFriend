package com.example.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.mobile.network.RegisterRequest
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OwnerRegisterActivity : Activity() {
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etAddress: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var tvError: TextView
    private lateinit var tvBack: TextView
    private lateinit var prefsManager: PreferencesManager

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_register)

        prefsManager = PreferencesManager(this)

        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etAddress = findViewById(R.id.etAddress)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        tvError = findViewById(R.id.tvError)
        tvBack = findViewById(R.id.tvBack)

        tvBack.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            registerUser()
        }
        
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun registerUser() {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        val phoneNumber = etPhoneNumber.text.toString().trim()
        val address = etAddress.text.toString().trim()

        clearError()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all required fields")
            return
        }

        if (!isValidPassword(password)) {
            showError("Password must be at least 8 characters with 1 uppercase, 1 number, and 1 special character")
            return
        }

        if (password != confirmPassword) {
            showError("Passwords do not match")
            return
        }

        val request = RegisterRequest(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            phoneNumber = phoneNumber.ifEmpty { null },
            address = address.ifEmpty { null },
            role = "PET_OWNER"
        )

        btnRegister.isEnabled = false

        scope.launch {
            try {
                val response = RetrofitClient.apiService.register(request)

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

                    Toast.makeText(this@OwnerRegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@OwnerRegisterActivity, DashboardActivity::class.java))
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = errorBody?.takeIf { it.isNotBlank() } ?: "Registration failed"
                    showError(message)
                    btnRegister.isEnabled = true
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
                btnRegister.isEnabled = true
            }
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun clearError() {
        tvError.text = ""
        tvError.visibility = View.GONE
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isDigit() } &&
                password.any { "!@#\$%^&*(),.?\":{}|<>".contains(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
