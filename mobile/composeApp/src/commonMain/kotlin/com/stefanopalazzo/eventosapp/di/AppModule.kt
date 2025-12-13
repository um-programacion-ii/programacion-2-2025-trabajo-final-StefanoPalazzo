package com.stefanopalazzo.eventosapp.di

import com.russhwolf.settings.Settings
import com.stefanopalazzo.eventosapp.data.api.ApiClient
import com.stefanopalazzo.eventosapp.data.api.ApiService
import com.stefanopalazzo.eventosapp.data.repository.AuthRepository
import com.stefanopalazzo.eventosapp.data.repository.EventoRepository
import com.stefanopalazzo.eventosapp.presentation.auth.LoginViewModel
import com.stefanopalazzo.eventosapp.presentation.auth.RegisterViewModel
import com.stefanopalazzo.eventosapp.presentation.checkout.CheckoutViewModel
import com.stefanopalazzo.eventosapp.presentation.events.EventDetailViewModel
import com.stefanopalazzo.eventosapp.presentation.events.EventListViewModel
import com.stefanopalazzo.eventosapp.presentation.seats.SeatSelectionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Settings
    single { Settings() }
    
    // API
    single { ApiClient(get()) }
    single { ApiService(get()) }
    
    // Repositories
    single { AuthRepository(get(), get()) }
    single { EventoRepository(get()) }
    
    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { EventListViewModel(get()) }
    viewModel { EventDetailViewModel(get()) }
    viewModel { SeatSelectionViewModel(get(), get()) }
    viewModel { CheckoutViewModel(get(), get()) }
}
