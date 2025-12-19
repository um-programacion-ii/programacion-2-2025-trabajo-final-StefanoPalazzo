package com.stefanopalazzo.eventosapp.presentation.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefanopalazzo.eventosapp.data.models.EventoResumido
import com.stefanopalazzo.eventosapp.data.repository.EventoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventListUiState(
    val eventos: List<EventoResumido> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class EventListViewModel(
    private val eventoRepository: EventoRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EventListUiState())
    val uiState: StateFlow<EventListUiState> = _uiState.asStateFlow()
    
    init {
        loadEventos()
    }
    
    fun loadEventos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            eventoRepository.getEventos()
                .onSuccess { eventos ->
                    _uiState.value = EventListUiState(eventos = eventos)
                }
                .onFailure { error ->
                    _uiState.value = EventListUiState(
                        error = error.message ?: "Error al cargar eventos"
                    )
                }
        }
    }
}
