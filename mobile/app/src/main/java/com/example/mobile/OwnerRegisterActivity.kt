package com.example.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.mobile.network.ApiClient
import com.example.mobile.network.RegisterRequest
import com.example.mobile.util.PreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    private lateinit var prefsManager: PreferencesManager

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

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 8 characters with 1 uppercase, 1 number, and 1 special character", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
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

        ApiClient.apiService.register(request).enqueue(object : Callback<com.example.mobile.network.AuthResponse> {
            override fun onResponse(call: Call<com.example.mobile.network.AuthResponse>, response: Response<com.example.mobile.network.AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    prefsManager.saveToken(authResponse.token)
                    Toast.makeText(this@OwnerRegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@OwnerRegisterActivity, DashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@OwnerRegisterActivity, "Registration failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<com.example.mobile.network.AuthResponse>, t: Throwable) {
                Toast.makeText(this@OwnerRegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isDigit() } &&
                password.any { "!@#\$%^&*(),.?\":{}|<>".contains(it) }
    }
}
