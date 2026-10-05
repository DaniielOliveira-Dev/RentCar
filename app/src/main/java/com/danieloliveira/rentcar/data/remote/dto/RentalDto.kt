package com.danieloliveira.rentcar.data.remote.dto

data class RentalDto(
    val localId: Long,
    val vehicleId: Long,
    val contactId: Long,
    val clientName: String,
    val startDate: String,
    val expectedReturnDate: String,
    val estimatedTotal: Double,
    val status: String
)
