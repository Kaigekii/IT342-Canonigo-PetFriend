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

data class ReviewSummaryResponse(
    val averageRating: Double,
    val reviewCount: Int
)

data class SitterProfileResponse(
    val profileId: String?,
    val userId: String?,
    val profilePhotoUrl: String?,
    val bio: String?,
    val experience: String?,
    val hourlyRate: Double?,
    val servicesOffered: List<String>?,
    val availabilitySchedule: Map<String, DayAvailability>?,
    val studentId: String?,
    val referenceContact: String?,
    val verificationDocumentUrl: String?,
    val location: String?,
    val isVerified: Boolean?
) {
    data class DayAvailability(
        val startTime: String?,
        val endTime: String?
    )
}

data class SitterProfileUpdateRequest(
    val profilePhotoUrl: String?,
    val bio: String?,
    val experience: String?,
    val hourlyRate: Double?,
    val servicesOffered: List<String>,
    val availabilitySchedule: Map<String, SitterProfileResponse.DayAvailability>,
    val location: String?,
    val studentId: String?,
    val referenceContact: String?,
    val verificationDocumentUrl: String?
)
