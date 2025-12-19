package com.stefanopalazzo.eventosapp.data.api

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient(private val settings: Settings) {
    
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
        }
        
        defaultRequest {
            contentType(ContentType.Application.Json)
            val token = settings.get<String>(TOKEN_KEY)
            if (token != null) {
                header("Authorization", "Bearer $token")
            }
        }
    }
    
    companion object {
        private const val TOKEN_KEY = "jwt_token"
    }
    
    fun saveToken(token: String) {
        settings[TOKEN_KEY] = token
    }
    
    fun getToken(): String? {
        return settings.get<String>(TOKEN_KEY)
    }
    
    fun clearToken() {
        settings.remove(TOKEN_KEY)
    }
    
    fun isAuthenticated(): Boolean {
        return getToken() != null
    }
}
