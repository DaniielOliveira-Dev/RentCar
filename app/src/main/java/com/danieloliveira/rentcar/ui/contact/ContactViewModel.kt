package com.danieloliveira.rentcar.ui.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danieloliveira.rentcar.data.contacts.DeviceContact
import com.danieloliveira.rentcar.data.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ContactUiState {
    data object Idle : ContactUiState
    data object Loading : ContactUiState
    data object Empty : ContactUiState
    data class Success(val contacts: List<DeviceContact>) : ContactUiState
    data class Error(val message: String) : ContactUiState
}

class ContactViewModel(
    private val repository: ContactRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ContactUiState>(ContactUiState.Idle)
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allContacts: List<DeviceContact> = emptyList()

    fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = ContactUiState.Loading
            runCatching { repository.getContacts() }
                .onSuccess { contacts ->
                    allContacts = contacts
                    applyFilter()
                }
                .onFailure {
                    _uiState.value = ContactUiState.Error("Não foi possível acessar os contatos do dispositivo.")
                }
        }
    }

    fun updateSearchQuery(value: String) {
        _searchQuery.value = value
        applyFilter()
    }

    private fun applyFilter() {
        val query = _searchQuery.value.trim()
        val filtered: List<DeviceContact> = if (query.isBlank()) {
            allContacts
        } else {
            allContacts.filter { it.name.contains(query, ignoreCase = true) }
        }

        _uiState.value = if (filtered.isEmpty()) {
            ContactUiState.Empty
        } else {
            ContactUiState.Success(filtered)
        }
    }
}
