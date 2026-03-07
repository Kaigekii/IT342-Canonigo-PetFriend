package com.example.mobile.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServiceNew {
    
    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>
    
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>
}
