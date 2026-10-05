package com.danieloliveira.rentcar.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import com.danieloliveira.rentcar.data.local.relation.RentalWithDetails
import com.danieloliveira.rentcar.domain.util.DateUtils
import com.danieloliveira.rentcar.ui.common.EmptyContent
import com.danieloliveira.rentcar.ui.common.ErrorContent
import com.danieloliveira.rentcar.ui.common.LoadingContent

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNewRental: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Locações Ativas",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            when (val state = uiState) {
                DashboardUiState.Loading -> LoadingContent()
                DashboardUiState.Empty -> EmptyContent("Nenhuma locação ativa.")
                is DashboardUiState.Error -> ErrorContent(state.message)
                is DashboardUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 96.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.rentals, key = { it.rental.id }) { rental ->
                            ActiveRentalCard(
                                rental = rental,
                                onFinish = {
                                    viewModel.finishRental(
                                        rentalId = rental.rental.id,
                                        vehicleId = rental.vehicle.id
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onNewRental,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+")
        }
    }
}

@Composable
private fun ActiveRentalCard(
    rental: RentalWithDetails,
    onFinish: () -> Unit
) {
    val daysRemaining = DateUtils.daysUntil(rental.rental.expectedReturnDate)
    val overdue = daysRemaining < 0

    val containerColor = if (overdue) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "${rental.vehicle.brand} ${rental.vehicle.model}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Placa: ${rental.vehicle.plate}")

            Spacer(Modifier.height(4.dp))

            Text(
                text = rental.client.name,
                fontWeight = FontWeight.SemiBold
            )
            Text("Telefone: ${rental.client.phone}")

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Saída", fontWeight = FontWeight.SemiBold)
                    Text(DateUtils.formatIsoDate(rental.rental.startDate))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Entrega prevista", fontWeight = FontWeight.SemiBold)
                    Text(DateUtils.formatIsoDate(rental.rental.expectedReturnDate))
                }
            }

            Text(
                text = when {
                    daysRemaining < 0 -> "${-daysRemaining} dia(s) em atraso"
                    daysRemaining == 0 -> "Entrega prevista para hoje"
                    else -> "$daysRemaining dia(s) restante(s)"
                },
                color = if (overdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp)
            )

            Button(
                onClick = onFinish,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Finalizar locação")
            }
        }
    }
}
