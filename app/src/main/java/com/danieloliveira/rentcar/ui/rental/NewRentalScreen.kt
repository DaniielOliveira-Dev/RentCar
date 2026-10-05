package com.danieloliveira.rentcar.ui.rental

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danieloliveira.rentcar.data.local.entity.VehicleEntity
import com.danieloliveira.rentcar.domain.util.DateUtils
import com.danieloliveira.rentcar.ui.common.EmptyContent
import com.danieloliveira.rentcar.ui.common.ErrorContent
import com.danieloliveira.rentcar.ui.common.LoadingContent
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

@Composable
fun NewRentalScreen(
    viewModel: NewRentalViewModel,
    selectedContactId: Long?,
    selectedContactName: String,
    selectedContactPhone: String,
    onSelectContact: () -> Unit,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val vehiclesState by viewModel.vehiclesState.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    var selectedVehicleId by rememberSaveable { mutableStateOf(-1L) }
    var startDateIso by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var expectedReturnDateIso by rememberSaveable {
        mutableStateOf(LocalDate.now().plusDays(1).toString())
    }

    val startDate = LocalDate.parse(startDateIso)
    val expectedReturnDate = LocalDate.parse(expectedReturnDateIso)

    val availableVehicles = when (val state = vehiclesState) {
        is AvailableVehiclesUiState.Success -> state.vehicles
        else -> emptyList()
    }
    val selectedVehicle = availableVehicles.firstOrNull { it.id == selectedVehicleId }
    val days = DateUtils.daysBetween(startDate, expectedReturnDate)
    val estimatedTotal = if (selectedVehicle != null && days > 0) {
        selectedVehicle.dailyRate * days
    } else {
        0.0
    }
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

    LaunchedEffect(saveState.saved) {
        if (saveState.saved) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Voltar")
        }

        Text(
            text = "Nova Locação",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "1. Veículo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        when (val state = vehiclesState) {
            AvailableVehiclesUiState.Loading -> LoadingContent()
            AvailableVehiclesUiState.Empty -> EmptyContent(
                "Não há veículos disponíveis. Cadastre um veículo ou finalize uma locação ativa."
            )
            is AvailableVehiclesUiState.Error -> ErrorContent(state.message)
            is AvailableVehiclesUiState.Success -> {
                state.vehicles.forEach { vehicle: VehicleEntity ->
                    SelectableVehicleCard(
                        vehicle = vehicle,
                        selected = vehicle.id == selectedVehicleId,
                        onClick = { selectedVehicleId = vehicle.id }
                    )
                }
            }
        }

        Text(
            text = "2. Cliente",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (selectedContactId != null && selectedContactId >= 0) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(selectedContactName, fontWeight = FontWeight.SemiBold)
                    Text(selectedContactPhone)
                }
            }
        } else {
            Text("Nenhum cliente selecionado.")
        }

        Button(
            onClick = onSelectContact,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (selectedContactId == null) "Selecionar cliente" else "Alterar cliente")
        }

        Text(
            text = "3. Período",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        RentalDatePickerField(
            label = "Data de saída",
            date = startDate,
            onDateSelected = { startDateIso = it.toString() }
        )

        RentalDatePickerField(
            label = "Entrega prevista",
            date = expectedReturnDate,
            onDateSelected = { expectedReturnDateIso = it.toString() }
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Resumo", fontWeight = FontWeight.Bold)
                Text("Quantidade de dias: ${if (days > 0) days else 0}")
                Text(
                    "Valor da diária: ${currency.format(selectedVehicle?.dailyRate ?: 0.0)}"
                )
                Text(
                    text = "Total estimado: ${currency.format(estimatedTotal)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        saveState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                viewModel.saveRental(
                    selectedVehicle = selectedVehicle,
                    contactId = selectedContactId,
                    clientName = selectedContactName,
                    clientPhone = selectedContactPhone,
                    startDate = startDate,
                    expectedReturnDate = expectedReturnDate
                )
            },
            enabled = !saveState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (saveState.isSaving) "Confirmando..." else "Confirmar locação")
        }
    }
}

@Composable
private fun SelectableVehicleCard(
    vehicle: VehicleEntity,
    selected: Boolean,
    onClick: () -> Unit
) {
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "${vehicle.brand} ${vehicle.model}",
                    fontWeight = FontWeight.SemiBold
                )
                Text(vehicle.plate)
            }
            Text(currency.format(vehicle.dailyRate))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RentalDatePickerField(
    label: String,
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Button(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("$label: ${DateUtils.format(date)}")
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = DateUtils.toUtcMillis(date)
        )

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(DateUtils.fromUtcMillis(millis))
                        }
                        showDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
