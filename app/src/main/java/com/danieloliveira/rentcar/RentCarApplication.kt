package com.danieloliveira.rentcar

import android.app.Application
import com.danieloliveira.rentcar.data.contacts.ContactDataSource
import com.danieloliveira.rentcar.data.local.AppDatabase
import com.danieloliveira.rentcar.data.remote.RetrofitClient
import com.danieloliveira.rentcar.data.repository.ContactRepository
import com.danieloliveira.rentcar.data.repository.RentalRepository
import com.danieloliveira.rentcar.data.repository.VehicleRepository

class RentCarApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val api by lazy { RetrofitClient.api }

    val vehicleRepository by lazy {
        VehicleRepository(database.vehicleDao(), api)
    }

    val rentalRepository by lazy {
        RentalRepository(database.rentalDao(), api)
    }

    val contactRepository by lazy {
        ContactRepository(ContactDataSource(contentResolver))
    }
}
