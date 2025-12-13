package com.stefanopalazzo.eventosapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventoResumido(
    val id: Long,
    val titulo: String,
    val resumen: String,
    val descripcion: String,
    val fecha: String,
    val precioEntrada: Double,
    val eventoTipo: EventoTipo
)

@Serializable
data class EventoCompleto(
    val id: Long,
    val titulo: String,
    val resumen: String,
    val descripcion: String,
    val fecha: String,
    val direccion: String,
    val imagen: String,
    val filaAsientos: Int,
    val columnAsientos: Int,
    val precioEntrada: Double,
    val eventoTipo: EventoTipo,
    val integrantes: List<Integrante>
)

@Serializable
data class EventoTipo(
    val nombre: String,
    val descripcion: String
)

@Serializable
data class Integrante(
    val nombre: String,
    val apellido: String,
    val identificacion: String
)
