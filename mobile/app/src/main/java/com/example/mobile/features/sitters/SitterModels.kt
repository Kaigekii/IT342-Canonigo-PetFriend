package com.example.mobile.features.sitters

data class SitterSummary(
    val sitterId: String,
    val fullName: String,
    val bio: String?,
    val experience: String?,
    val hourlyRate: Double?,
    val servicesOffered: List<String> = emptyList(),
    val rating: Double?,
    val reviewCount: Long?,
    val verified: Boolean,
    val location: String?
)

data class SitterDetail(
    val sitterId: String,
    val fullName: String,
    val bio: String?,
    val experience: String?,
    val hourlyRate: Double?,
    val servicesOffered: List<String> = emptyList(),
    val location: String?,
    val availabilitySchedule: Map<String, DayAvailability> = emptyMap(),
    val rating: Double?,
    val reviewCount: Long?,
    val verified: Boolean,
    val reviews: List<SitterReview> = emptyList()
)

data class SitterReview(
    val reviewerName: String,
    val date: String,
    val rating: Int,
    val comment: String
)

data class DayAvailability(
    val startTime: String?,
    val endTime: String?
)
