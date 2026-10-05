package com.danieloliveira.rentcar.data.remote

import com.danieloliveira.rentcar.data.remote.dto.MockSyncResponseDto
import com.danieloliveira.rentcar.data.remote.dto.RentalDto
import com.danieloliveira.rentcar.data.remote.dto.VehicleDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RentalApi {
    // JSONPlaceholder é utilizado somente como endpoint REST mock para a atividade.
    @POST("posts")
    suspend fun syncVehicle(@Body vehicle: VehicleDto): Response<MockSyncResponseDto>

    @POST("posts")
    suspend fun syncRental(@Body rental: RentalDto): Response<MockSyncResponseDto>
}
