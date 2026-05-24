package com.example.mobile.features.bookings

data class Booking(
    val bookingId: String,
    val petOwnerId: String,
    val petSitterId: String,
    val sitterFirstName: String,
    val sitterLastName: String,
    val ownerFirstName: String,
    val ownerLastName: String,
    val serviceType: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val totalPrice: Double
)
