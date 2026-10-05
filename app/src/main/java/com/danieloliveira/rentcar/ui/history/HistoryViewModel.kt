package com.danieloliveira.rentcar.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danieloliveira.rentcar.data.local.relation.RentalWithDetails
import com.danieloliveira.rentcar.data.repository.RentalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data object Empty : HistoryUiState
    data class Success(val rentals: List<RentalWithDetails>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

class HistoryViewModel(
    private val repository: RentalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.rentalHistory
                .catch { _uiState.value = HistoryUiState.Error("Não foi possível carregar o histórico.") }
                .collect { rentals ->
                    _uiState.value = if (rentals.isEmpty()) {
                        HistoryUiState.Empty
                    } else {
                        HistoryUiState.Success(rentals)
                    }
                }
        }
    }
}
