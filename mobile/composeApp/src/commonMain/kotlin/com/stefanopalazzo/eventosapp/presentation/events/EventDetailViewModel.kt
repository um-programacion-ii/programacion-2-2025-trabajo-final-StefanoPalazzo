package com.stefanopalazzo.eventosapp.presentation.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefanopalazzo.eventosapp.data.models.EventoCompleto
import com.stefanopalazzo.eventosapp.data.repository.EventoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventDetailUiState(
    val evento: EventoCompleto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class EventDetailViewModel(
    private val eventoRepository: EventoRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()
    
    fun loadEvento(id: Long) {
        viewModelScope.launch {
            _uiState.value = EventDetailUiState(isLoading = true)
            
            eventoRepository.getEventoCompleto(id)
                .onSuccess { evento ->
                    _uiState.value = EventDetailUiState(evento = evento)
                }
                .onFailure { error ->
                    _uiState.value = EventDetailUiState(
                        error = error.message ?: "Error al cargar el evento"
                    )
                }
        }
    }
}
