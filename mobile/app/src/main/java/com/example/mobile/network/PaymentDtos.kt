package com.example.mobile.network

data class PaymentCheckoutRequest(
    val bookingId: String
)

data class PaymentCheckoutResponse(
    val checkoutUrl: String,
    val paymentId: String? = null
)