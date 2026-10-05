package com.danieloliveira.rentcar.data.repository

import com.danieloliveira.rentcar.data.local.dao.RentalDao
import com.danieloliveira.rentcar.data.local.relation.RentalWithDetails
import com.danieloliveira.rentcar.data.remote.RentalApi
import com.danieloliveira.rentcar.data.remote.dto.RentalDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withTimeoutOrNull

class RentalRepository(
    private val rentalDao: RentalDao,
    private val api: RentalApi
) {
    val activeRentals: Flow<List<RentalWithDetails>> = rentalDao.observeActive()
    val rentalHistory: Flow<List<RentalWithDetails>> = rentalDao.observeHistory()

    suspend fun createRental(
        vehicleId: Long,
        contactId: Long,
        clientName: String,
        clientPhone: String,
        startDate: String,
        expectedReturnDate: String,
        estimatedTotal: Double
    ) {
        val rentalId = rentalDao.createRental(
            vehicleId = vehicleId,
            contactId = contactId,
            clientName = clientName,
            clientPhone = clientPhone,
            startDate = startDate,
            expectedReturnDate = expectedReturnDate,
            estimatedTotal = estimatedTotal
        )

        // Sincronização REST mockada exigida pelo projeto.
        runCatching {
            withTimeoutOrNull(1500) {
                api.syncRental(
                    RentalDto(
                        localId = rentalId,
                        vehicleId = vehicleId,
                        contactId = contactId,
                        clientName = clientName,
                        startDate = startDate,
                        expectedReturnDate = expectedReturnDate,
                        estimatedTotal = estimatedTotal,
                        status = "ATIVA"
                    )
                )
            }
        }
    }

    suspend fun finishRental(
        rentalId: Long,
        vehicleId: Long,
        actualReturnDate: String
    ) {
        rentalDao.finishRental(rentalId, vehicleId, actualReturnDate)
    }
}
