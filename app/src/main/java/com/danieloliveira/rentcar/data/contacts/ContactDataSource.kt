package com.danieloliveira.rentcar.data.contacts

import android.content.ContentResolver
import android.provider.ContactsContract

class ContactDataSource(
    private val contentResolver: ContentResolver
) {
    fun getContacts(): List<DeviceContact> {
        val contacts = mutableListOf<DeviceContact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            )
            val nameIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
            val phoneIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex).orEmpty().trim()
                val phone = cursor.getString(phoneIndex).orEmpty().trim()

                if (name.isNotBlank() && phone.isNotBlank()) {
                    contacts += DeviceContact(
                        id = cursor.getLong(idIndex),
                        name = name,
                        phone = phone
                    )
                }
            }
        }

        return contacts.distinctBy { "${it.id}-${it.phone}" }
    }
}
