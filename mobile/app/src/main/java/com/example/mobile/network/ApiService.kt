package com.example.mobile.network

import com.example.mobile.features.pets.Pet
import com.example.mobile.features.bookings.Booking
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @GET("api/user/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserProfileResponse>
    
    @GET("api/pets")
    suspend fun getMyPets(@Header("Authorization") token: String): Response<List<Pet>>
    
    @GET("api/bookings")
    suspend fun getMyBookings(
        @Header("Authorization") token: String,
        @Query("upcoming") upcoming: Boolean = false
    ): Response<List<Booking>>
}
