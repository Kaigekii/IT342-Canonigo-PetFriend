package com.example.mobile.network

import com.example.mobile.features.pets.Pet
import com.example.mobile.features.pets.PetUpsertRequest
import com.example.mobile.features.bookings.Booking
import com.example.mobile.features.bookings.CreateBookingRequest
import com.example.mobile.features.bookings.UpdateOwnerBookingStatusRequest
import com.example.mobile.features.bookings.UpdateSitterBookingStatusRequest
import com.example.mobile.features.messages.CreateThreadRequest
import com.example.mobile.features.messages.MessageItem
import com.example.mobile.features.messages.MessageThread
import com.example.mobile.features.messages.SendMessageRequest
import com.example.mobile.features.sitters.SitterDetail
import com.example.mobile.features.sitters.SitterSummary
import com.example.mobile.network.ReviewSummaryResponse
import com.example.mobile.network.SitterProfileResponse
import com.example.mobile.network.SitterProfileUpdateRequest
import com.example.mobile.network.PaymentCheckoutRequest
import com.example.mobile.network.PaymentCheckoutResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): Response<AuthResponse>
    
    @GET("api/user/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserProfileResponse>
    
    @GET("api/pets")
    suspend fun getMyPets(@Header("Authorization") token: String): Response<List<Pet>>

    @POST("api/pets")
    suspend fun createPet(
        @Header("Authorization") token: String,
        @Body request: PetUpsertRequest
    ): Response<Pet>

    @PUT("api/pets/{petId}")
    suspend fun updatePet(
        @Header("Authorization") token: String,
        @Path("petId") petId: String,
        @Body request: PetUpsertRequest
    ): Response<Pet>

    @DELETE("api/pets/{petId}")
    suspend fun deletePet(
        @Header("Authorization") token: String,
        @Path("petId") petId: String
    ): Response<Map<String, String>>
    
    @GET("api/bookings")
    suspend fun getMyBookings(
        @Header("Authorization") token: String,
        @Query("upcoming") upcoming: Boolean = false
    ): Response<List<Booking>>

    @POST("api/bookings")
    suspend fun createBooking(
        @Header("Authorization") token: String,
        @Body request: CreateBookingRequest
    ): Response<Booking>

    @POST("api/payments/paymongo/checkout")
    suspend fun createPaymentCheckout(
        @Header("Authorization") token: String,
        @Body request: PaymentCheckoutRequest
    ): Response<PaymentCheckoutResponse>

    @PUT("api/bookings/{bookingId}/owner-status")
    suspend fun updateOwnerBookingStatus(
        @Header("Authorization") token: String,
        @Path("bookingId") bookingId: String,
        @Body request: UpdateOwnerBookingStatusRequest
    ): Response<Booking>

    @GET("api/bookings/sitter")
    suspend fun getSitterBookings(
        @Header("Authorization") token: String
    ): Response<List<Booking>>

    @GET("api/bookings/sitter/pending")
    suspend fun getSitterPendingBookings(
        @Header("Authorization") token: String
    ): Response<List<Booking>>

    @GET("api/bookings/sitter/upcoming")
    suspend fun getSitterUpcomingBookings(
        @Header("Authorization") token: String
    ): Response<List<Booking>>

    @GET("api/bookings/sitter/today")
    suspend fun getSitterTodayBookings(
        @Header("Authorization") token: String
    ): Response<List<Booking>>

    @PUT("api/bookings/{bookingId}/sitter-status")
    suspend fun updateSitterBookingStatus(
        @Header("Authorization") token: String,
        @Path("bookingId") bookingId: String,
        @Body request: UpdateSitterBookingStatusRequest
    ): Response<Booking>

    @GET("api/sitters/search")
    suspend fun searchSitters(
        @Header("Authorization") token: String,
        @Query("location") location: String? = null,
        @Query("serviceType") serviceType: String? = null
    ): Response<List<SitterSummary>>

    @GET("api/sitters/{sitterId}")
    suspend fun getSitterDetails(
        @Header("Authorization") token: String,
        @Path("sitterId") sitterId: String
    ): Response<SitterDetail>

    @GET("api/messages/threads")
    suspend fun listMessageThreads(
        @Header("Authorization") token: String
    ): Response<List<MessageThread>>

    @POST("api/messages/threads")
    suspend fun createMessageThread(
        @Header("Authorization") token: String,
        @Body request: CreateThreadRequest
    ): Response<MessageThread>

    @GET("api/messages/threads/{threadId}/messages")
    suspend fun listThreadMessages(
        @Header("Authorization") token: String,
        @Path("threadId") threadId: String
    ): Response<List<MessageItem>>

    @POST("api/messages/threads/{threadId}/messages")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Path("threadId") threadId: String,
        @Body request: SendMessageRequest
    ): Response<MessageItem>

    @GET("api/reviews/sitter/{sitterId}/summary")
    suspend fun getSitterReviewSummary(
        @Header("Authorization") token: String,
        @Path("sitterId") sitterId: String
    ): Response<ReviewSummaryResponse>

    @GET("api/sitter-profile")
    suspend fun getSitterProfile(
        @Header("Authorization") token: String
    ): Response<SitterProfileResponse>

    @GET("api/sitters/profile")
    suspend fun getSitterProfileAlt(
        @Header("Authorization") token: String
    ): Response<SitterProfileResponse>

    @PUT("api/sitter-profile")
    suspend fun updateSitterProfile(
        @Header("Authorization") token: String,
        @Body request: SitterProfileUpdateRequest
    ): Response<SitterProfileResponse>

    @PUT("api/sitters/profile")
    suspend fun updateSitterProfileAlt(
        @Header("Authorization") token: String,
        @Body request: SitterProfileUpdateRequest
    ): Response<SitterProfileResponse>

    @POST("api/sitter-profile/submit-verification")
    suspend fun submitSitterVerification(
        @Header("Authorization") token: String
    ): Response<String>

    @POST("api/sitters/profile/submit-verification")
    suspend fun submitSitterVerificationAlt(
        @Header("Authorization") token: String
    ): Response<String>
}
