package com.danieloliveira.rentcar.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import com.danieloliveira.rentcar.data.local.entity.ClientEntity
import com.danieloliveira.rentcar.data.local.entity.RentalEntity
import com.danieloliveira.rentcar.data.local.relation.RentalWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RentalDao {

    @Transaction
    @Query("SELECT * FROM rentals WHERE status = 'ATIVA' ORDER BY expectedReturnDate")
    abstract fun observeActive(): Flow<List<RentalWithDetails>>

    @Transaction
    @Query("SELECT * FROM rentals WHERE status = 'FINALIZADA' ORDER BY actualReturnDate DESC, id DESC")
    abstract fun observeHistory(): Flow<List<RentalWithDetails>>

    @Query("SELECT status FROM vehicles WHERE id = :vehicleId LIMIT 1")
    protected abstract suspend fun getVehicleStatus(vehicleId: Long): String?

    @Query("SELECT id FROM clients WHERE contactId = :contactId LIMIT 1")
    protected abstract suspend fun findClientId(contactId: Long): Long?

    @Insert
    protected abstract suspend fun insertClient(client: ClientEntity): Long

    @Query("UPDATE clients SET name = :name, phone = :phone WHERE id = :clientId")
    protected abstract suspend fun updateClient(clientId: Long, name: String, phone: String)

    @Insert
    protected abstract suspend fun insertRental(rental: RentalEntity): Long

    @Query("UPDATE vehicles SET status = :status WHERE id = :vehicleId")
    protected abstract suspend fun updateVehicleStatus(vehicleId: Long, status: String)

    @Query("UPDATE rentals SET status = 'FINALIZADA', actualReturnDate = :actualReturnDate WHERE id = :rentalId")
    protected abstract suspend fun markRentalFinished(rentalId: Long, actualReturnDate: String)

    @Transaction
    open suspend fun createRental(
        vehicleId: Long,
        contactId: Long,
        clientName: String,
        clientPhone: String,
        startDate: String,
        expectedReturnDate: String,
        estimatedTotal: Double
    ): Long {
        val vehicleStatus = getVehicleStatus(vehicleId)
            ?: throw IllegalArgumentException("Veículo não encontrado.")

        if (vehicleStatus != "DISPONIVEL") {
            throw IllegalStateException("O veículo selecionado não está disponível.")
        }

        val existingClientId = findClientId(contactId)
        val clientId = if (existingClientId == null) {
            insertClient(
                ClientEntity(
                    contactId = contactId,
                    name = clientName,
                    phone = clientPhone
                )
            )
        } else {
            updateClient(existingClientId, clientName, clientPhone)
            existingClientId
        }

        val rentalId = insertRental(
            RentalEntity(
                vehicleId = vehicleId,
                clientId = clientId,
                startDate = startDate,
                expectedReturnDate = expectedReturnDate,
                estimatedTotal = estimatedTotal
            )
        )

        updateVehicleStatus(vehicleId, "ALUGADO")
        return rentalId
    }

    @Transaction
    open suspend fun finishRental(
        rentalId: Long,
        vehicleId: Long,
        actualReturnDate: String
    ) {
        markRentalFinished(rentalId, actualReturnDate)
        updateVehicleStatus(vehicleId, "DISPONIVEL")
    }
}
