package com.stefanopalazzo.eventosapp.data.repository

import com.stefanopalazzo.eventosapp.data.api.ApiService
import com.stefanopalazzo.eventosapp.data.models.EventoCompleto
import com.stefanopalazzo.eventosapp.data.models.EventoResumido

class EventoRepository(
    private val apiService: ApiService
) {
    
    suspend fun getEventos(): Result<List<EventoResumido>> {
        return apiService.getEventos()
    }
    
    suspend fun getEventoCompleto(id: Long): Result<EventoCompleto> {
        return apiService.getEventoCompleto(id)
    }
}
