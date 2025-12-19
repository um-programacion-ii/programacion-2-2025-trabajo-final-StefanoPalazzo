package com.stefanopalazzo.eventosapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stefanopalazzo.eventosapp.presentation.auth.LoginScreen
import com.stefanopalazzo.eventosapp.presentation.auth.LoginViewModel
import com.stefanopalazzo.eventosapp.presentation.auth.RegisterScreen
import com.stefanopalazzo.eventosapp.presentation.auth.RegisterViewModel
import com.stefanopalazzo.eventosapp.presentation.checkout.CheckoutScreen
import com.stefanopalazzo.eventosapp.presentation.checkout.CheckoutViewModel
import com.stefanopalazzo.eventosapp.presentation.events.EventDetailScreen
import com.stefanopalazzo.eventosapp.presentation.events.EventDetailViewModel
import com.stefanopalazzo.eventosapp.presentation.events.EventListScreen
import com.stefanopalazzo.eventosapp.presentation.events.EventListViewModel
import com.stefanopalazzo.eventosapp.presentation.profile.ProfileScreen
import com.stefanopalazzo.eventosapp.presentation.profile.ProfileViewModel
import com.stefanopalazzo.eventosapp.presentation.seats.SeatSelectionScreen
import com.stefanopalazzo.eventosapp.presentation.seats.SeatSelectionViewModel
import com.stefanopalazzo.eventosapp.presentation.tickets.MyTicketsScreen
import com.stefanopalazzo.eventosapp.presentation.tickets.MyTicketsViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Ocultar barra de navegación en login y registro
        val showBottomBar = currentRoute !in listOf("login", "register")

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Home, contentDescription = "Eventos") },
                            label = { Text("Eventos") },
                            selected = currentRoute == "event_list",
                            onClick = {
                                navController.navigate("event_list") {
                                    popUpTo("event_list") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.DateRange, contentDescription = "Mis Entradas") },
                            label = { Text("Entradas") },
                            selected = currentRoute == "tickets",
                            onClick = {
                                navController.navigate("tickets") {
                                    popUpTo("event_list") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
                            label = { Text("Perfil") },
                            selected = currentRoute == "profile",
                            onClick = {
                                navController.navigate("profile") {
                                    popUpTo("event_list") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("login") {
                    val viewModel = koinViewModel<LoginViewModel>()
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {
                            navController.navigate("event_list") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onNavigateToRegister = { navController.navigate("register") }
                    )
                }

                composable("register") {
                    val viewModel = koinViewModel<RegisterViewModel>()
                    RegisterScreen(
                        viewModel = viewModel,
                        onRegisterSuccess = { navController.popBackStack() },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("event_list") {
                    val viewModel = koinViewModel<EventListViewModel>()
                    EventListScreen(
                        viewModel = viewModel,
                        onEventClick = { eventoId ->
                            navController.navigate("event_detail/$eventoId")
                        }
                    )
                }

                composable("tickets") {
                    val viewModel = koinViewModel<MyTicketsViewModel>()
                    MyTicketsScreen(viewModel = viewModel)
                }

                composable("profile") {
                    val viewModel = koinViewModel<ProfileViewModel>()
                    ProfileScreen(
                        viewModel = viewModel,
                        onLogoutSuccess = {
                            navController.navigate("login") {
                                popUpTo("event_list") { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = "event_detail/{eventoId}",
                    arguments = listOf(navArgument("eventoId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val eventoId = backStackEntry.arguments?.getLong("eventoId") ?: 0L
                    val viewModel = koinViewModel<EventDetailViewModel>()
                    EventDetailScreen(
                        viewModel = viewModel,
                        eventoId = eventoId,
                        onBackClick = { navController.popBackStack() },
                        onSelectSeatsClick = { id ->
                            navController.navigate("seat_selection/$id")
                        }
                    )
                }

                composable(
                    route = "seat_selection/{eventoId}",
                    arguments = listOf(navArgument("eventoId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val eventoId = backStackEntry.arguments?.getLong("eventoId") ?: 0L
                    val viewModel = koinViewModel<SeatSelectionViewModel>()
                    SeatSelectionScreen(
                        viewModel = viewModel,
                        eventoId = eventoId,
                        onBackClick = { navController.popBackStack() },
                        onNavigateToCheckout = { id, asientos ->
                            navController.navigate("checkout/$id/$asientos")
                        }
                    )
                }

                composable(
                    route = "checkout/{eventoId}/{asientos}",
                    arguments = listOf(
                        navArgument("eventoId") { type = NavType.LongType },
                        navArgument("asientos") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val eventoId = backStackEntry.arguments?.getLong("eventoId") ?: 0L
                    val asientosStr = backStackEntry.arguments?.getString("asientos") ?: ""
                    val viewModel = koinViewModel<CheckoutViewModel>()
                    CheckoutScreen(
                        viewModel = viewModel,
                        eventoId = eventoId,
                        asientosStr = asientosStr,
                        onBackClick = { navController.popBackStack() },
                        onSuccess = {
                            // Ir a Mis Entradas después de comprar
                            navController.navigate("tickets") {
                                popUpTo("event_list") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}