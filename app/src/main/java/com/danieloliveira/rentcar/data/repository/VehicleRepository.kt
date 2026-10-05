package com.danieloliveira.rentcar.data.repository

import com.danieloliveira.rentcar.data.local.dao.VehicleDao
import com.danieloliveira.rentcar.data.local.entity.VehicleEntity
import com.danieloliveira.rentcar.data.remote.RentalApi
import com.danieloliveira.rentcar.data.remote.dto.VehicleDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withTimeoutOrNull

class VehicleRepository(
    private val vehicleDao: VehicleDao,
    private val api: RentalApi
) {
    val vehicles: Flow<List<VehicleEntity>> = vehicleDao.observeAll()
    val availableVehicles: Flow<List<VehicleEntity>> = vehicleDao.observeAvailable()

    suspend fun addVehicle(vehicle: VehicleEntity) {
        if (vehicleDao.countByPlate(vehicle.plate) > 0) {
            throw IllegalArgumentException("Já existe um veículo cadastrado com esta placa.")
        }

        val id = vehicleDao.insert(vehicle)
        val savedVehicle = vehicle.copy(id = id)

        // Sincronização mockada via Retrofit. Falhas de rede não impedem o uso local do app.
        runCatching {
            withTimeoutOrNull(1500) {
                api.syncVehicle(VehicleDto.fromEntity(savedVehicle))
            }
        }
    }
}
