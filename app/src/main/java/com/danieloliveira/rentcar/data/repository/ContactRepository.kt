package com.danieloliveira.rentcar.data.repository

import com.danieloliveira.rentcar.data.contacts.ContactDataSource
import com.danieloliveira.rentcar.data.contacts.DeviceContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactRepository(
    private val contactDataSource: ContactDataSource
) {
    suspend fun getContacts(): List<DeviceContact> = withContext(Dispatchers.IO) {
        contactDataSource.getContacts()
    }
}
