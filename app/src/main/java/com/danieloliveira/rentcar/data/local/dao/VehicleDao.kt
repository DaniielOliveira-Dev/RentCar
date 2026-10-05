package com.danieloliveira.rentcar.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.danieloliveira.rentcar.data.local.entity.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY brand, model")
    fun observeAll(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE status = 'DISPONIVEL' ORDER BY brand, model")
    fun observeAvailable(): Flow<List<VehicleEntity>>

    @Insert
    suspend fun insert(vehicle: VehicleEntity): Long

    @Query("SELECT COUNT(*) FROM vehicles WHERE plate = :plate")
    suspend fun countByPlate(plate: String): Int
}
