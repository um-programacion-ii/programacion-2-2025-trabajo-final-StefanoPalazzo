package com.stefanopalazzo.eventosapp.presentation.seats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefanopalazzo.eventosapp.data.api.ApiService
import com.stefanopalazzo.eventosapp.data.models.AsientoRedis
import com.stefanopalazzo.eventosapp.data.models.AsientoSimple
import com.stefanopalazzo.eventosapp.data.models.BloquearAsientosRequest
import com.stefanopalazzo.eventosapp.data.models.EventoCompleto
import com.stefanopalazzo.eventosapp.data.repository.EventoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SeatSelectionUiState(
    val evento: EventoCompleto? = null,
    val asientosOcupados: List<AsientoRedis> = emptyList(),
    val asientosSeleccionados: List<AsientoSimple> = emptyList(),
    val isLoading: Boolean = false,
    val isBlocking: Boolean = false,
    val error: String? = null,
    val navigateToCheckout: Boolean = false
)

class SeatSelectionViewModel(
    private val eventoRepository: EventoRepository,
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SeatSelectionUiState())
    val uiState: StateFlow<SeatSelectionUiState> = _uiState.asStateFlow()
    
    fun loadData(eventoId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Cargar datos del evento
            val eventoResult = eventoRepository.getEventoCompleto(eventoId)
            
            if (eventoResult.isSuccess) {
                val evento = eventoResult.getOrNull()
                _uiState.value = _uiState.value.copy(evento = evento)
                
                // Cargar mapa de asientos (Redis)
                apiService.getMapaAsientos(eventoId)
                    .onSuccess { mapa ->
                        _uiState.value = _uiState.value.copy(
                            asientosOcupados = mapa.asientos,
                            isLoading = false
                        )
                    }
                    .onFailure { error ->
                        // Si falla Redis, asumimos que no hay asientos ocupados (o mostramos error)
                        // Para robustez, permitimos continuar pero mostramos advertencia si es crítico
                        println("Error cargando mapa de asientos: ${error.message}")
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar información del evento"
                )
            }
        }
    }
    
    fun toggleSeatSelection(fila: Int, columna: Int) {
        val currentSelection = _uiState.value.asientosSeleccionados.toMutableList()
        val seat = AsientoSimple(fila, columna)
        
        if (currentSelection.contains(seat)) {
            currentSelection.remove(seat)
        } else {
            if (currentSelection.size < 4) {
                currentSelection.add(seat)
            } else {
                // Max 4 seats reached
                return
            }
        }
        
        _uiState.value = _uiState.value.copy(asientosSeleccionados = currentSelection)
    }
    
    fun bloquearAsientos() {
        val evento = _uiState.value.evento ?: return
        val seleccion = _uiState.value.asientosSeleccionados
        
        if (seleccion.isEmpty()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBlocking = true)
            
            val request = BloquearAsientosRequest(
                eventoId = evento.id,
                asientos = seleccion
            )
            
            apiService.bloquearAsientos(request)
                .onSuccess { response ->
                    if (response.resultado) {
                        _uiState.value = _uiState.value.copy(
                            isBlocking = false,
                            navigateToCheckout = true
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isBlocking = false,
                            error = "No se pudieron bloquear los asientos: ${response.descripcion}"
                        )
                        // Recargar mapa para mostrar qué asientos fallaron (ya estaban ocupados)
                        loadData(evento.id)
                    }
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isBlocking = false,
                        error = error.message ?: "Error al bloquear asientos"
                    )
                }
        }
    }
    
    fun navigationHandled() {
        _uiState.value = _uiState.value.copy(navigateToCheckout = false)
    }
}
