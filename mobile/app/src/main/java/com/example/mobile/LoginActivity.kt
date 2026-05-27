package com.example.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.util.Log
import com.example.mobile.network.LoginRequest
import com.example.mobile.network.GoogleAuthRequest
import com.example.mobile.network.RetrofitClient
import com.example.mobile.network.SupabaseAuthService
import com.example.mobile.util.PreferencesManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : Activity() {
    
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogleLogin: Button
    private lateinit var tvGoToRegister: TextView
    private lateinit var tvError: TextView
    
    private lateinit var prefsManager: PreferencesManager
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var supabaseAuthService: SupabaseAuthService

    private val googleSignInClient by lazy {
        GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private companion object {
        const val RC_GOOGLE_SIGN_IN = 1201
        const val TAG = "LoginActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        prefsManager = PreferencesManager(this)
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        supabaseAuthService = SupabaseAuthService(
            getString(R.string.supabase_url),
            getString(R.string.supabase_anon_key)
        )
        
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
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)
        tvError = findViewById(R.id.tvError)
    }
    
    private fun setupListeners() {
        btnLogin.setOnClickListener {
            performLogin()
        }

        btnGoogleLogin.setOnClickListener {
            startGoogleSignIn()
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

    private fun startGoogleSignIn() {
        clearError()
        btnGoogleLogin.isEnabled = false
        googleSignInClient.signOut().addOnCompleteListener {
            btnGoogleLogin.isEnabled = true
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != RC_GOOGLE_SIGN_IN) return

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken.isNullOrBlank()) {
                Log.e(TAG, "Google sign-in failed: missing idToken")
                showError("Google sign-in failed: missing token")
                return
            }
            exchangeGoogleToken(idToken, null)
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign-in failed: ${e.statusCode}")
            showError("Google sign-in failed (${e.statusCode})")
        }
    }

    private fun exchangeGoogleToken(idToken: String, role: String?) {
        btnGoogleLogin.isEnabled = false
        scope.launch {
            try {
                val supabaseToken = withContext(Dispatchers.IO) {
                    supabaseAuthService.exchangeGoogleIdToken(idToken)
                }
                val accessToken = supabaseToken.access_token
                if (accessToken.isNullOrBlank()) {
                    Log.e(TAG, "Supabase token exchange returned empty access_token")
                    showError("Google sign-in failed: no access token")
                    btnGoogleLogin.isEnabled = true
                    return@launch
                }

                val response = RetrofitClient.apiService.googleAuth(
                    GoogleAuthRequest(token = accessToken, role = role)
                )

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
                    val errorBody = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                    Log.e(TAG, "Backend googleAuth failed: ${response.code()} ${errorBody ?: ""}")
                    val msg = errorBody ?: "Google sign-in failed (${response.code()})"
                    showError(msg)
                    btnGoogleLogin.isEnabled = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Google sign-in error", e)
                showError("Error: ${e.message}")
                btnGoogleLogin.isEnabled = true
            }
        }
    }
    
    private fun navigateToDashboard() {
        val role = prefsManager.getRole()
        if (role == "PET_OWNER") {
            startActivity(Intent(this, com.example.mobile.features.dashboard.OwnerMainActivity::class.java))
        } else if (role == "PET_SITTER") {
            startActivity(Intent(this, com.example.mobile.features.dashboard.SitterMainActivity::class.java))
        } else {
            prefsManager.clearAll()
            Toast.makeText(this, "Unable to determine role. Please login again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }
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
