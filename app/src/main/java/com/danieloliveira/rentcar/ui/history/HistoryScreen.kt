package com.danieloliveira.rentcar.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danieloliveira.rentcar.data.local.relation.RentalWithDetails
import com.danieloliveira.rentcar.domain.util.DateUtils
import com.danieloliveira.rentcar.ui.common.EmptyContent
import com.danieloliveira.rentcar.ui.common.ErrorContent
import com.danieloliveira.rentcar.ui.common.LoadingContent
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Histórico de Locações",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        when (val state = uiState) {
            HistoryUiState.Loading -> LoadingContent()
            HistoryUiState.Empty -> EmptyContent("Nenhuma locação finalizada.")
            is HistoryUiState.Error -> ErrorContent(state.message)
            is HistoryUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.rentals, key = { it.rental.id }) { rental ->
                        HistoryRentalCard(rental)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRentalCard(rental: RentalWithDetails) {
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${rental.vehicle.brand} ${rental.vehicle.model} - ${rental.vehicle.plate}",
                fontWeight = FontWeight.Bold
            )
            Text("Cliente: ${rental.client.name}")
            Text("Telefone: ${rental.client.phone}")
            Text("Saída: ${DateUtils.formatIsoDate(rental.rental.startDate)}")
            Text("Previsão: ${DateUtils.formatIsoDate(rental.rental.expectedReturnDate)}")
            rental.rental.actualReturnDate?.let {
                Text("Devolução: ${DateUtils.formatIsoDate(it)}")
            }
            Text("Valor estimado: ${currency.format(rental.rental.estimatedTotal)}")
        }
    }
}
