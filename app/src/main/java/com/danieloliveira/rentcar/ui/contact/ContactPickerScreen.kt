package com.danieloliveira.rentcar.ui.contact

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danieloliveira.rentcar.data.contacts.DeviceContact
import com.danieloliveira.rentcar.ui.common.EmptyContent
import com.danieloliveira.rentcar.ui.common.ErrorContent
import com.danieloliveira.rentcar.ui.common.LoadingContent

@Composable
fun ContactPickerScreen(
    viewModel: ContactViewModel,
    onBack: () -> Unit,
    onContactSelected: (DeviceContact) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var permissionGranted by rememberSaveable {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDenied by rememberSaveable { mutableStateOf(false) }
    var permissionAsked by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        permissionDenied = !granted
        if (granted) viewModel.loadContacts()
    }

    LaunchedEffect(Unit) {
        if (permissionGranted) {
            viewModel.loadContacts()
        } else if (!permissionAsked) {
            permissionAsked = true
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Voltar")
        }

        Text(
            text = "Selecionar Cliente",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (!permissionGranted) {
            Text(
                text = if (permissionDenied) {
                    "A permissão de leitura de contatos foi negada. Ela é necessária para selecionar o cliente da locação."
                } else {
                    "O aplicativo precisa acessar a agenda para permitir a seleção do cliente."
                }
            )
            Button(
                onClick = {
                    permissionAsked = true
                    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            ) {
                Text("Tentar novamente")
            }
            return@Column
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            label = { Text("Buscar por nome") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        when (val state = uiState) {
            ContactUiState.Idle -> Unit
            ContactUiState.Loading -> LoadingContent()
            ContactUiState.Empty -> EmptyContent("Nenhum contato encontrado.")
            is ContactUiState.Error -> ErrorContent(
                message = state.message,
                onRetry = viewModel::loadContacts
            )
            is ContactUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.contacts,
                        key = { "${it.id}-${it.phone}" }
                    ) { contact ->
                        ContactCard(
                            contact = contact,
                            onClick = { onContactSelected(contact) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactCard(
    contact: DeviceContact,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = contact.name,
                fontWeight = FontWeight.SemiBold
            )
            Text(contact.phone)
        }
    }
}
