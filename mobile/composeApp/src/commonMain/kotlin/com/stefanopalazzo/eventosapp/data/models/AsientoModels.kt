package com.stefanopalazzo.eventosapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AsientoRedis(
    val fila: Int,
    val columna: Int,
    val estado: String, // "Bloqueado" o "Vendido"
    val expira: String? = null // Solo para bloqueados
)

@Serializable
data class MapaAsientos(
    val eventoId: Long,
    val asientos: List<AsientoRedis>
)

@Serializable
data class BloquearAsientosRequest(
    val eventoId: Long,
    val asientos: List<AsientoSimple>
)

@Serializable
data class BloquearAsientosResponse(
    val resultado: Boolean,
    val descripcion: String,
    val eventoId: Long,
    val asientos: List<AsientoConEstado>
)

@Serializable
data class AsientoSimple(
    val fila: Int,
    val columna: Int
)

@Serializable
data class AsientoConEstado(
    val fila: Int,
    val columna: Int,
    val estado: String
)

@Serializable
data class AsientoConPersona(
    val fila: Int,
    val columna: Int,
    val persona: String
)

@Serializable
data class RealizarVentaRequest(
    val eventoId: Long,
    val fecha: String,
    val precioVenta: Double,
    val asientos: List<AsientoConPersona>
)

@Serializable
data class RealizarVentaResponse(
    val eventoId: Long,
    val ventaId: Long?,
    val fechaVenta: String,
    val asientos: List<AsientoConEstado>,
    val resultado: Boolean,
    val descripcion: String,
    val precioVenta: Double
)
