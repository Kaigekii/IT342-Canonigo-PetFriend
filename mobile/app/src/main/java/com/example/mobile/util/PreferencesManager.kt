package com.example.mobile.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_ADDRESS = "address"
        private const val KEY_ROLE = "role"
        private const val KEY_IS_VERIFIED = "is_verified"
    }
    
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    fun saveToken(token: String) {
        saveAuthToken(token)
    }
    
    fun getAuthToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }
    
    fun getToken(): String? {
        return getAuthToken()
    }
    
    fun saveUserData(
        userId: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String?,
        address: String?,
        role: String?,
        isVerified: Boolean?
    ) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_FIRST_NAME, firstName)
            putString(KEY_LAST_NAME, lastName)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE_NUMBER, phoneNumber)
            putString(KEY_ADDRESS, address)
            putString(KEY_ROLE, role)

            if (isVerified == null) {
                remove(KEY_IS_VERIFIED)
            } else {
                putBoolean(KEY_IS_VERIFIED, isVerified)
            }

            apply()
        }
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }
    
    fun getFirstName(): String? {
        return prefs.getString(KEY_FIRST_NAME, null)
    }
    
    fun getLastName(): String? {
        return prefs.getString(KEY_LAST_NAME, null)
    }
    
    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    fun getPhoneNumber(): String? {
        return prefs.getString(KEY_PHONE_NUMBER, null)
    }
    
    fun getAddress(): String? {
        return prefs.getString(KEY_ADDRESS, null)
    }

    fun getRole(): String? {
        return prefs.getString(KEY_ROLE, null)
    }

    fun getIsVerified(): Boolean? {
        return if (prefs.contains(KEY_IS_VERIFIED)) prefs.getBoolean(KEY_IS_VERIFIED, false) else null
    }
    
    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
