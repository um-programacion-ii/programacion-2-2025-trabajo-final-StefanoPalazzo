package com.stefanopalazzo.eventosapp.data.repository

import com.stefanopalazzo.eventosapp.data.api.ApiClient
import com.stefanopalazzo.eventosapp.data.api.ApiService

class AuthRepository(
    private val apiService: ApiService,
    private val apiClient: ApiClient
) {
    
    suspend fun login(username: String, password: String): Result<String> {
        return apiService.login(username, password).map { response ->
            apiClient.saveToken(response.token)
            response.token
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return apiService.logout()
    }
    
    fun isAuthenticated(): Boolean {
        return apiClient.isAuthenticated()
    }
    
    fun getToken(): String? {
        return apiClient.getToken()
    }
}
