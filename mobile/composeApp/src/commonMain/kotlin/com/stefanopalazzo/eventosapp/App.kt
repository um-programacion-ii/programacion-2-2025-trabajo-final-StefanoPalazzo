package com.stefanopalazzo.eventosapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stefanopalazzo.eventosapp.di.appModule
import com.stefanopalazzo.eventosapp.presentation.auth.LoginScreen
import com.stefanopalazzo.eventosapp.presentation.events.EventListScreen
import org.koin.compose.KoinApplication

import com.stefanopalazzo.eventosapp.presentation.auth.RegisterScreen
import com.stefanopalazzo.eventosapp.presentation.seats.SeatSelectionScreen
import com.stefanopalazzo.eventosapp.presentation.checkout.CheckoutScreen
import com.stefanopalazzo.eventosapp.presentation.events.EventDetailScreen


@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        MaterialTheme {
            val navController = rememberNavController()
            
            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("events") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onNavigateToRegister = {
                            navController.navigate("register")
                        }
                    )
                }
                
                composable("register") {
                    RegisterScreen(
                        onRegisterSuccess = {
                            navController.popBackStack() // Volver al login
                        },
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
                
                composable("events") {
                    EventListScreen(
                        onEventClick = { eventoId ->
                            navController.navigate("event_detail/$eventoId")
                        }
                    )
                }
                
                composable("event_detail/{eventoId}") { backStackEntry ->
                    val eventoId = backStackEntry.arguments?.getString("eventoId")?.toLongOrNull() ?: 0L
                    EventDetailScreen(
                        eventoId = eventoId,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onSelectSeatsClick = { id ->
                            navController.navigate("seat_selection/$id")
                        }
                    )
                }
                
                composable("seat_selection/{eventoId}") { backStackEntry ->
                    val eventoId = backStackEntry.arguments?.getString("eventoId")?.toLongOrNull() ?: 0L
                    SeatSelectionScreen(
                        eventoId = eventoId,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onNavigateToCheckout = { id, asientos ->
                            navController.navigate("checkout/$id/$asientos")
                        }
                    )
                }
                
                composable("checkout/{eventoId}/{asientos}") { backStackEntry ->
                    val eventoId = backStackEntry.arguments?.getString("eventoId")?.toLongOrNull() ?: 0L
                    val asientos = backStackEntry.arguments?.getString("asientos") ?: ""
                    
                    CheckoutScreen(
                        eventoId = eventoId,
                        asientosStr = asientos,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onSuccess = {
                            // Volver al inicio (lista de eventos)
                            navController.navigate("events") {
                                popUpTo("events") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}