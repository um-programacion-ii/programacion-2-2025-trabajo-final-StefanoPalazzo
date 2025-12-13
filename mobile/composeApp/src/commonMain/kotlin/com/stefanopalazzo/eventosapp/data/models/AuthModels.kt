package com.stefanopalazzo.eventosapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String
)

@Serializable
data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val username: String? = null
)

@Serializable
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: String
)
