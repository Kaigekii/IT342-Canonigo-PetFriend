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
        private const val KEY_AGE = "age"
        private const val KEY_GENDER = "gender"
        private const val KEY_ADDRESS = "address"
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
        id: Long,
        firstName: String,
        lastName: String,
        email: String,
        age: Int?,
        gender: String?,
        address: String?
    ) {
        prefs.edit().apply {
            putLong(KEY_USER_ID, id)
            putString(KEY_FIRST_NAME, firstName)
            putString(KEY_LAST_NAME, lastName)
            putString(KEY_EMAIL, email)
            age?.let { putInt(KEY_AGE, it) }
            gender?.let { putString(KEY_GENDER, it) }
            address?.let { putString(KEY_ADDRESS, it) }
            apply()
        }
    }
    
    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1)
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
    
    fun getAge(): Int {
        return prefs.getInt(KEY_AGE, 0)
    }
    
    fun getGender(): String? {
        return prefs.getString(KEY_GENDER, null)
    }
    
    fun getAddress(): String? {
        return prefs.getString(KEY_ADDRESS, null)
    }
    
    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
