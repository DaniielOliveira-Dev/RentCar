package com.danieloliveira.rentcar.ui.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danieloliveira.rentcar.data.local.entity.VehicleEntity
import com.danieloliveira.rentcar.data.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface VehicleListUiState {
    data object Loading : VehicleListUiState
    data object Empty : VehicleListUiState
    data class Success(val vehicles: List<VehicleEntity>) : VehicleListUiState
    data class Error(val message: String) : VehicleListUiState
}

class VehicleViewModel(
    private val repository: VehicleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<VehicleListUiState>(VehicleListUiState.Loading)
    val uiState: StateFlow<VehicleListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.vehicles
                .catch { _uiState.value = VehicleListUiState.Error("Não foi possível carregar os veículos.") }
                .collect { vehicles ->
                    _uiState.value = if (vehicles.isEmpty()) {
                        VehicleListUiState.Empty
                    } else {
                        VehicleListUiState.Success(vehicles)
                    }
                }
        }
    }
}
