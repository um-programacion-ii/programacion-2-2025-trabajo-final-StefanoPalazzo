package com.stefanopalazzo.eventosapp.data.api

import kotlinx.serialization.json.Json
import com.stefanopalazzo.eventosapp.data.models.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class ApiService(private val apiClient: ApiClient) {
    
    private val client = apiClient.client
    private val baseUrl = ApiConfig.BASE_URL
    
    // Auth
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = client.post("$baseUrl/api/login") {
                setBody(LoginRequest(username, password))
            }
            if (response.status == io.ktor.http.HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                // Try to parse error response
                try {
                    val error = response.body<ErrorResponse>()
                    Result.failure(Exception(error.message))
                } catch (e: Exception) {
                    Result.failure(Exception("Error en login: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = client.post("$baseUrl/api/register") {
                setBody(request)
            }
            // Backend returns 201 Created on success, 400 on failure
            if (response.status == io.ktor.http.HttpStatusCode.Created || response.status == io.ktor.http.HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                try {
                    val error = response.body<RegisterResponse>() // Backend returns RegisterResponse even on error (success=false)
                    if (!error.success) {
                         Result.failure(Exception(error.message))
                    } else {
                         Result.failure(Exception("Error en registro: ${response.status}"))
                    }
                } catch (e: Exception) {
                     Result.failure(Exception("Error en registro: ${response.status}"))
                }
            }
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
            val response = client.get("$baseUrl/api/catedra/eventos")
            if (response.status == io.ktor.http.HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Error al cargar eventos: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getEventoCompleto(id: Long): Result<EventoCompleto> {
        return try {
            val response = client.get("$baseUrl/api/catedra/eventos/$id")
            if (response.status == io.ktor.http.HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Error al cargar evento: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Asientos
    suspend fun getMapaAsientos(eventoId: Long): Result<MapaAsientos> {
        return try {
            // Backend devuelve List<AsientoRedis> directamente
            val response = client.get("$baseUrl/api/asientos/$eventoId")
            if (response.status == io.ktor.http.HttpStatusCode.OK) {
                val asientos = response.body<List<AsientoRedis>>()
                Result.success(MapaAsientos(eventoId, asientos))
            } else {
                Result.failure(Exception("Error al cargar asientos: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun bloquearAsientos(request: BloquearAsientosRequest): Result<BloquearAsientosResponse> {
        return try {
            val response = client.post("$baseUrl/api/catedra/bloquear-asientos") {
                setBody(request)
            }
            if (response.status == io.ktor.http.HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Error al bloquear asientos: ${response.status}"))
            }
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
