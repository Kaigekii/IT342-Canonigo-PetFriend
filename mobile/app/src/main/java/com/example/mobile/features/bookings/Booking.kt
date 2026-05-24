package com.example.mobile.features.bookings

data class Booking(
    val bookingId: String,
    val ownerId: String,
    val ownerName: String,
    val sitterId: String?,
    val sitterName: String?,
    val serviceType: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val petNames: List<String> = emptyList(),
    val petIds: List<String> = emptyList(),
    val totalAmount: Double? = null,
    val currency: String? = null
)

data class CreateBookingRequest(
    val sitterId: String,
    val petIds: List<String>,
    val serviceType: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val specialInstructions: String? = null
)

data class UpdateOwnerBookingStatusRequest(
    val status: String
)

data class UpdateSitterBookingStatusRequest(
    val status: String
)
