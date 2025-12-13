package com.stefanopalazzo.eventosapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefanopalazzo.eventosapp.data.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _logoutState = MutableStateFlow<Boolean>(false)
    val logoutState: StateFlow<Boolean> = _logoutState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            apiService.logout()
            _logoutState.value = true
        }
    }
    
    fun resetLogoutState() {
        _logoutState.value = false
    }
}
