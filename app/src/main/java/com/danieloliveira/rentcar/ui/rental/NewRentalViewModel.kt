package com.danieloliveira.rentcar.ui.rental

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danieloliveira.rentcar.data.local.entity.VehicleEntity
import com.danieloliveira.rentcar.data.repository.RentalRepository
import com.danieloliveira.rentcar.data.repository.VehicleRepository
import com.danieloliveira.rentcar.domain.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface AvailableVehiclesUiState {
    data object Loading : AvailableVehiclesUiState
    data object Empty : AvailableVehiclesUiState
    data class Success(val vehicles: List<VehicleEntity>) : AvailableVehiclesUiState
    data class Error(val message: String) : AvailableVehiclesUiState
}

data class NewRentalSaveState(
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null
)

class NewRentalViewModel(
    vehicleRepository: VehicleRepository,
    private val rentalRepository: RentalRepository
) : ViewModel() {
    private val _vehiclesState = MutableStateFlow<AvailableVehiclesUiState>(AvailableVehiclesUiState.Loading)
    val vehiclesState: StateFlow<AvailableVehiclesUiState> = _vehiclesState.asStateFlow()

    private val _saveState = MutableStateFlow(NewRentalSaveState())
    val saveState: StateFlow<NewRentalSaveState> = _saveState.asStateFlow()

    init {
        viewModelScope.launch {
            vehicleRepository.availableVehicles
                .catch { _vehiclesState.value = AvailableVehiclesUiState.Error("Não foi possível carregar os veículos disponíveis.") }
                .collect { vehicles ->
                    _vehiclesState.value = if (vehicles.isEmpty()) {
                        AvailableVehiclesUiState.Empty
                    } else {
                        AvailableVehiclesUiState.Success(vehicles)
                    }
                }
        }
    }

    fun saveRental(
        selectedVehicle: VehicleEntity?,
        contactId: Long?,
        clientName: String,
        clientPhone: String,
        startDate: LocalDate,
        expectedReturnDate: LocalDate
    ) {
        if (selectedVehicle == null) {
            _saveState.value = NewRentalSaveState(errorMessage = "Selecione um veículo disponível.")
            return
        }

        if (contactId == null || clientName.isBlank() || clientPhone.isBlank()) {
            _saveState.value = NewRentalSaveState(errorMessage = "Selecione um cliente da agenda de contatos.")
            return
        }

        val days = DateUtils.daysBetween(startDate, expectedReturnDate)
        if (days <= 0) {
            _saveState.value = NewRentalSaveState(
                errorMessage = "A data prevista de entrega deve ser posterior à data de saída."
            )
            return
        }

        val total = days * selectedVehicle.dailyRate

        viewModelScope.launch {
            _saveState.value = NewRentalSaveState(isSaving = true)
            runCatching {
                rentalRepository.createRental(
                    vehicleId = selectedVehicle.id,
                    contactId = contactId,
                    clientName = clientName,
                    clientPhone = clientPhone,
                    startDate = startDate.toString(),
                    expectedReturnDate = expectedReturnDate.toString(),
                    estimatedTotal = total
                )
            }.onSuccess {
                _saveState.value = NewRentalSaveState(saved = true)
            }.onFailure { error ->
                _saveState.value = NewRentalSaveState(
                    errorMessage = error.message ?: "Não foi possível registrar a locação."
                )
            }
        }
    }
}
