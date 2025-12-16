package com.stefanopalazzo.eventosapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefanopalazzo.eventosapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    fun register(username: String, password: String, email: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)
            
            authRepository.register(username.lowercase(), password, email, firstName, lastName)
                .onSuccess { success ->
                    if (success) {
                        _uiState.value = RegisterUiState(isSuccess = true)
                    } else {
                        _uiState.value = RegisterUiState(error = "El registro falló sin mensaje de error")
                    }
                }
                .onFailure { error ->
                    _uiState.value = RegisterUiState(
                        error = error.message ?: "Error desconocido"
                    )
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun resetState() {
        _uiState.value = RegisterUiState()
    }
}
