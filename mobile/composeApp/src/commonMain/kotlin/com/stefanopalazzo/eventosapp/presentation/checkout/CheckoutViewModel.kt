package com.stefanopalazzo.eventosapp.presentation.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefanopalazzo.eventosapp.data.api.ApiService
import com.stefanopalazzo.eventosapp.data.models.AsientoConPersona
import com.stefanopalazzo.eventosapp.data.models.AsientoSimple
import com.stefanopalazzo.eventosapp.data.models.EventoCompleto
import com.stefanopalazzo.eventosapp.data.models.RealizarVentaRequest
import com.stefanopalazzo.eventosapp.data.repository.EventoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class CheckoutUiState(
    val evento: EventoCompleto? = null,
    val asientos: List<AsientoSimple> = emptyList(),
    val nombres: Map<String, String> = emptyMap(), // Key: "fila:columna", Value: Nombre
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class CheckoutViewModel(
    private val eventoRepository: EventoRepository,
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()
    
    fun loadData(eventoId: Long, asientosStr: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Parsear asientos
            val asientos = try {
                asientosStr.split(",").map { 
                    val parts = it.split(":")
                    AsientoSimple(parts[0].toInt(), parts[1].toInt())
                }
            } catch (e: Exception) {
                emptyList()
            }
            
            if (asientos.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "No hay asientos seleccionados"
                )
                return@launch
            }
            
            _uiState.value = _uiState.value.copy(asientos = asientos)
            
            // Cargar evento
            eventoRepository.getEventoCompleto(eventoId)
                .onSuccess { evento ->
                    _uiState.value = _uiState.value.copy(
                        evento = evento,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar evento"
                    )
                }
        }
    }
    
    fun updateNombre(fila: Int, columna: Int, nombre: String) {
        val key = "$fila:$columna"
        val currentNombres = _uiState.value.nombres.toMutableMap()
        currentNombres[key] = nombre
        _uiState.value = _uiState.value.copy(nombres = currentNombres)
    }
    
    fun confirmarVenta() {
        val state = _uiState.value
        val evento = state.evento ?: return
        
        // Validar que todos los asientos tengan nombre
        val allNamesFilled = state.asientos.all { asiento ->
            val key = "${asiento.fila}:${asiento.columna}"
            !state.nombres[key].isNullOrBlank()
        }
        
        if (!allNamesFilled) {
            _uiState.value = state.copy(error = "Por favor complete todos los nombres")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = state.copy(isProcessing = true, error = null)
            
            val asientosConPersona = state.asientos.map { asiento ->
                val key = "${asiento.fila}:${asiento.columna}"
                AsientoConPersona(
                    fila = asiento.fila,
                    columna = asiento.columna,
                    persona = state.nombres[key] ?: ""
                )
            }
            
            val request = RealizarVentaRequest(
                eventoId = evento.id,
                fecha = Clock.System.now().toString(),
                precioVenta = evento.precioEntrada * state.asientos.size,
                asientos = asientosConPersona
            )
            
            apiService.realizarVenta(request)
                .onSuccess { response ->
                    if (response.resultado) {
                        _uiState.value = state.copy(
                            isProcessing = false,
                            isSuccess = true
                        )
                    } else {
                        _uiState.value = state.copy(
                            isProcessing = false,
                            error = "Venta rechazada: ${response.descripcion}"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.value = state.copy(
                        isProcessing = false,
                        error = error.message ?: "Error al procesar la venta"
                    )
                }
        }
    }
    
    fun resetSuccessState() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}
