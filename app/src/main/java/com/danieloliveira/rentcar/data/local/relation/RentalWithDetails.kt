package com.danieloliveira.rentcar.data.local.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import com.danieloliveira.rentcar.data.local.entity.ClientEntity
import com.danieloliveira.rentcar.data.local.entity.RentalEntity
import com.danieloliveira.rentcar.data.local.entity.VehicleEntity

data class RentalWithDetails(
    @Embedded
    val rental: RentalEntity,

    @Relation(
        parentColumns = ["vehicleId"],
        entityColumns = ["id"]
    )
    val vehicle: VehicleEntity,

    @Relation(
        parentColumns = ["clientId"],
        entityColumns = ["id"]
    )
    val client: ClientEntity
)