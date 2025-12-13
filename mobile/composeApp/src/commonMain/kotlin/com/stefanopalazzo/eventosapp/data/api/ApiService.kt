package com.stefanopalazzo.eventosapp.data.api

import com.stefanopalazzo.eventosapp.data.models.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class ApiService(private val apiClient: ApiClient) {
    
    private val client = apiClient.client
    private val baseUrl = ApiClient.BASE_URL
    
    // Auth
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = client.post("$baseUrl/api/login") {
                setBody(LoginRequest(username, password))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return try {
            client.post("$baseUrl/api/logout")
            apiClient.clearToken()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Eventos
    suspend fun getEventos(): Result<List<EventoResumido>> {
        return try {
            val response = client.get("$baseUrl/api/catedra/eventos-resumidos")
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getEventoCompleto(id: Long): Result<EventoCompleto> {
        return try {
            val response = client.get("$baseUrl/api/catedra/evento/$id")
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Asientos
    suspend fun getMapaAsientos(eventoId: Long): Result<MapaAsientos> {
        return try {
            val response = client.get("$baseUrl/proxy/asientos/$eventoId")
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun bloquearAsientos(request: BloquearAsientosRequest): Result<BloquearAsientosResponse> {
        return try {
            val response = client.post("$baseUrl/proxy/bloquear-asientos") {
                setBody(request)
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Venta
    suspend fun realizarVenta(request: RealizarVentaRequest): Result<RealizarVentaResponse> {
        return try {
            val response = client.post("$baseUrl/api/carrito/finalizar/${request.eventoId}") {
                setBody(request)
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
