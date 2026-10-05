package com.danieloliveira.rentcar.ui.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danieloliveira.rentcar.data.local.entity.VehicleEntity
import com.danieloliveira.rentcar.data.repository.VehicleRepository
import com.danieloliveira.rentcar.domain.validator.VehicleValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VehicleFormUiState(
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null
)

class VehicleFormViewModel(
    private val repository: VehicleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleFormUiState())
    val uiState: StateFlow<VehicleFormUiState> = _uiState.asStateFlow()

    fun saveVehicle(
        brand: String,
        model: String,
        plate: String,
        year: String,
        dailyRate: String
    ) {
        val validationError = VehicleValidator.validate(
            brand = brand,
            model = model,
            plate = plate,
            year = year,
            dailyRate = dailyRate
        )

        if (validationError != null) {
            _uiState.value = VehicleFormUiState(errorMessage = validationError)
            return
        }

        viewModelScope.launch {
            _uiState.value = VehicleFormUiState(isSaving = true)

            runCatching {
                repository.addVehicle(
                    VehicleEntity(
                        brand = brand.trim(),
                        model = model.trim(),
                        plate = VehicleValidator.normalizePlate(plate),
                        year = year.toInt(),
                        dailyRate = dailyRate.replace(',', '.').toDouble()
                    )
                )
            }.onSuccess {
                _uiState.value = VehicleFormUiState(saved = true)
            }.onFailure { error ->
                _uiState.value = VehicleFormUiState(
                    errorMessage = error.message ?: "Não foi possível salvar o veículo."
                )
            }
        }
    }
}
