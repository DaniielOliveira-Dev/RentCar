package com.danieloliveira.rentcar.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.danieloliveira.rentcar.RentCarApplication
import com.danieloliveira.rentcar.ui.common.SimpleViewModelFactory
import com.danieloliveira.rentcar.ui.contact.ContactPickerScreen
import com.danieloliveira.rentcar.ui.contact.ContactViewModel
import com.danieloliveira.rentcar.ui.dashboard.DashboardScreen
import com.danieloliveira.rentcar.ui.dashboard.DashboardViewModel
import com.danieloliveira.rentcar.ui.history.HistoryScreen
import com.danieloliveira.rentcar.ui.history.HistoryViewModel
import com.danieloliveira.rentcar.ui.rental.NewRentalScreen
import com.danieloliveira.rentcar.ui.rental.NewRentalViewModel
import com.danieloliveira.rentcar.ui.vehicle.VehicleFormScreen
import com.danieloliveira.rentcar.ui.vehicle.VehicleFormViewModel
import com.danieloliveira.rentcar.ui.vehicle.VehicleListScreen
import com.danieloliveira.rentcar.ui.vehicle.VehicleViewModel

private data class BottomItem(
    val route: String,
    val label: String,
    val shortLabel: String
)

private val bottomItems = listOf(
    BottomItem(Routes.DASHBOARD, "Dashboard", "D"),
    BottomItem(Routes.VEHICLES, "Veículos", "V"),
    BottomItem(Routes.HISTORY, "Histórico", "H")
)

@Composable
fun RentCarApp(application: RentCarApplication) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text(item.shortLabel) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) {
                val vm: DashboardViewModel = viewModel(
                    factory = SimpleViewModelFactory {
                        DashboardViewModel(application.rentalRepository)
                    }
                )
                DashboardScreen(
                    viewModel = vm,
                    onNewRental = { navController.navigate(Routes.NEW_RENTAL) }
                )
            }

            composable(Routes.VEHICLES) {
                val vm: VehicleViewModel = viewModel(
                    factory = SimpleViewModelFactory {
                        VehicleViewModel(application.vehicleRepository)
                    }
                )
                VehicleListScreen(
                    viewModel = vm,
                    onAddVehicle = { navController.navigate(Routes.VEHICLE_FORM) }
                )
            }

            composable(Routes.VEHICLE_FORM) {
                val vm: VehicleFormViewModel = viewModel(
                    factory = SimpleViewModelFactory {
                        VehicleFormViewModel(application.vehicleRepository)
                    }
                )
                VehicleFormScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.NEW_RENTAL) { entry ->
                val vm: NewRentalViewModel = viewModel(
                    factory = SimpleViewModelFactory {
                        NewRentalViewModel(
                            vehicleRepository = application.vehicleRepository,
                            rentalRepository = application.rentalRepository
                        )
                    }
                )

                val contactIdValue by entry.savedStateHandle
                    .getStateFlow("contact_id", -1L)
                    .collectAsStateWithLifecycle()
                val contactName by entry.savedStateHandle
                    .getStateFlow("contact_name", "")
                    .collectAsStateWithLifecycle()
                val contactPhone by entry.savedStateHandle
                    .getStateFlow("contact_phone", "")
                    .collectAsStateWithLifecycle()

                NewRentalScreen(
                    viewModel = vm,
                    selectedContactId = contactIdValue.takeIf { it >= 0 },
                    selectedContactName = contactName,
                    selectedContactPhone = contactPhone,
                    onSelectContact = { navController.navigate(Routes.CONTACTS) },
                    onBack = { navController.popBackStack() },
                    onSaved = {
                        navController.popBackStack(Routes.DASHBOARD, false)
                    }
                )
            }

            composable(Routes.CONTACTS) {
                val vm: ContactViewModel = viewModel(
                    factory = SimpleViewModelFactory {
                        ContactViewModel(application.contactRepository)
                    }
                )

                ContactPickerScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onContactSelected = { contact ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("contact_id", contact.id)
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("contact_name", contact.name)
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("contact_phone", contact.phone)
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.HISTORY) {
                val vm: HistoryViewModel = viewModel(
                    factory = SimpleViewModelFactory {
                        HistoryViewModel(application.rentalRepository)
                    }
                )
                HistoryScreen(viewModel = vm)
            }
        }
    }
}
