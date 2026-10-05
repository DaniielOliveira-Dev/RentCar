package com.danieloliveira.rentcar.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danieloliveira.rentcar.data.local.relation.RentalWithDetails
import com.danieloliveira.rentcar.data.repository.RentalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data object Empty : DashboardUiState
    data class Success(val rentals: List<RentalWithDetails>) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(
    private val repository: RentalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.activeRentals
                .catch { _uiState.value = DashboardUiState.Error("Não foi possível carregar as locações.") }
                .collect { rentals ->
                    _uiState.value = if (rentals.isEmpty()) {
                        DashboardUiState.Empty
                    } else {
                        DashboardUiState.Success(rentals)
                    }
                }
        }
    }

    fun finishRental(rentalId: Long, vehicleId: Long) {
        viewModelScope.launch {
            runCatching {
                repository.finishRental(
                    rentalId = rentalId,
                    vehicleId = vehicleId,
                    actualReturnDate = LocalDate.now().toString()
                )
            }
        }
    }
}
