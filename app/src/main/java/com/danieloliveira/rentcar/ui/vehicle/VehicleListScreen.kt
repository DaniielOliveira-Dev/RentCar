package com.danieloliveira.rentcar.ui.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danieloliveira.rentcar.data.local.entity.VehicleEntity
import com.danieloliveira.rentcar.domain.model.VehicleStatus
import com.danieloliveira.rentcar.ui.common.EmptyContent
import com.danieloliveira.rentcar.ui.common.ErrorContent
import com.danieloliveira.rentcar.ui.common.LoadingContent
import java.text.NumberFormat
import java.util.Locale

@Composable
fun VehicleListScreen(
    viewModel: VehicleViewModel,
    onAddVehicle: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Frota de Veículos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            when (val state = uiState) {
                VehicleListUiState.Loading -> LoadingContent()
                VehicleListUiState.Empty -> EmptyContent("Nenhum veículo cadastrado.")
                is VehicleListUiState.Error -> ErrorContent(state.message)
                is VehicleListUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 96.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.vehicles, key = { it.id }) { vehicle ->
                            VehicleCard(vehicle)
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddVehicle,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+")
        }
    }
}

@Composable
private fun VehicleCard(vehicle: VehicleEntity) {
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

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
            Text(
                text = "${vehicle.brand} ${vehicle.model}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Placa: ${vehicle.plate}")
            Text("Ano: ${vehicle.year}")
            Text("Diária: ${currency.format(vehicle.dailyRate)}")
            Text(
                text = "Status: ${VehicleStatus.labelOf(vehicle.status)}",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
