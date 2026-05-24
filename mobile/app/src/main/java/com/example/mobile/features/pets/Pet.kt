package com.example.mobile.features.pets

data class Pet(
    val petId: String,
    val ownerId: String,
    val ownerName: String?,
    val name: String,
    val breed: String?,
    val age: Int?,
    val weight: Double?,
    val species: String,
    val specialNeeds: String?,
    val vaccinationStatus: String?,
    val photoUrl: String?
)

data class PetUpsertRequest(
    val name: String,
    val breed: String?,
    val age: Int?,
    val weight: Double?,
    val species: String,
    val specialNeeds: String?,
    val vaccinationStatus: String,
    val photoUrl: String?
)
