package com.danieloliveira.rentcar.data.remote.dto

import com.danieloliveira.rentcar.data.local.entity.VehicleEntity

data class VehicleDto(
    val localId: Long,
    val brand: String,
    val model: String,
    val plate: String,
    val year: Int,
    val dailyRate: Double,
    val status: String
) {
    companion object {
        fun fromEntity(vehicle: VehicleEntity) = VehicleDto(
            localId = vehicle.id,
            brand = vehicle.brand,
            model = vehicle.model,
            plate = vehicle.plate,
            year = vehicle.year,
            dailyRate = vehicle.dailyRate,
            status = vehicle.status
        )
    }
}
