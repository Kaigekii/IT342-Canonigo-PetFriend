package com.example.mobile.network

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phoneNumber: String? = null,
    val address: String? = null,
    val role: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String? = null,
    val address: String? = null,
    val role: String,
    val isVerified: Boolean? = null
)

data class UserProfileResponse(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String? = null,
    val address: String? = null,
    val role: String,
    val isVerified: Boolean? = null
)
