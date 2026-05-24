package com.example.mobile.features.pets

data class Pet(
    val petId: String,
    val name: String,
    val species: String,
    val breed: String?,
    val age: Int?,
    val weight: Double?,
    val profilePhotoUrl: String?,
    val ownerId: String
)
