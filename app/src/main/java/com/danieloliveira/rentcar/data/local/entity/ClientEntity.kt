package com.danieloliveira.rentcar.data.local.entity

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "clients",
    indices = [Index(value = ["contactId"], unique = true)]
)
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactId: Long,
    val name: String,
    val phone: String
)
