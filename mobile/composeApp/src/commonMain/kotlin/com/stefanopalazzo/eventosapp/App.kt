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
                        }
                    )
                }
                
                composable("events") {
                    EventListScreen(
                        onEventClick = { eventoId ->
                            // TODO: Navigate to event detail
                            println("Clicked evento: $eventoId")
                        }
                    )
                }
            }
        }
    }
}