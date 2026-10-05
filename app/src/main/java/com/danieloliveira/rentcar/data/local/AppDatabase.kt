package com.danieloliveira.rentcar.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.danieloliveira.rentcar.data.local.dao.ClientDao
import com.danieloliveira.rentcar.data.local.dao.RentalDao
import com.danieloliveira.rentcar.data.local.dao.VehicleDao
import com.danieloliveira.rentcar.data.local.entity.ClientEntity
import com.danieloliveira.rentcar.data.local.entity.RentalEntity
import com.danieloliveira.rentcar.data.local.entity.VehicleEntity

@Database(
    entities = [VehicleEntity::class, ClientEntity::class, RentalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun clientDao(): ClientDao
    abstract fun rentalDao(): RentalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rentcar_database"
                )
                    .setDriver(AndroidSQLiteDriver())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
