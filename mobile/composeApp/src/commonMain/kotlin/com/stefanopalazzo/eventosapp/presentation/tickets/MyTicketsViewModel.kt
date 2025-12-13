package com.stefanopalazzo.eventosapp.presentation.tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefanopalazzo.eventosapp.data.api.ApiService
import com.stefanopalazzo.eventosapp.data.models.RealizarVentaResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TicketUiModel(
    val venta: RealizarVentaResponse,
    val nombreEvento: String
)

class MyTicketsViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyTicketsUiState>(MyTicketsUiState.Loading)
    val uiState: StateFlow<MyTicketsUiState> = _uiState.asStateFlow()

    fun loadTickets() {
        viewModelScope.launch {
            _uiState.value = MyTicketsUiState.Loading
            
            // 1. Obtener lista de ventas
            val ventasResult = apiService.listarVentas()
            
            ventasResult.onFailure { error ->
                _uiState.value = MyTicketsUiState.Error(error.message ?: "Error al cargar ventas")
                return@launch
            }
            
            val ventasResumidas = ventasResult.getOrNull() ?: emptyList()
            
            if (ventasResumidas.isEmpty()) {
                _uiState.value = MyTicketsUiState.Empty
                return@launch
            }

            // 2. Cargar detalles completos de ventas en paralelo
            val ventasDetalladas = ventasResumidas.map { venta ->
                async {
                    val id = venta.ventaId
                    if (id != null) {
                        apiService.getVenta(id).getOrDefault(venta)
                    } else {
                        venta
                    }
                }
            }.awaitAll()

            // 3. Obtener nombres de eventos
            // Obtenemos todos los eventos para hacer un mapa id -> nombre
            val eventosResult = apiService.getEventos()
            val mapaEventos = eventosResult.getOrNull()?.associate { it.id to it.titulo } ?: emptyMap()

            // 4. Combinar información
            val tickets = ventasDetalladas.map { venta ->
                val nombreEvento = mapaEventos[venta.eventoId] ?: "Evento #${venta.eventoId}"
                TicketUiModel(venta, nombreEvento)
            }

            _uiState.value = MyTicketsUiState.Success(tickets)
        }
    }
}

sealed class MyTicketsUiState {
    object Loading : MyTicketsUiState()
    object Empty : MyTicketsUiState()
    data class Success(val tickets: List<TicketUiModel>) : MyTicketsUiState()
    data class Error(val message: String) : MyTicketsUiState()
}
