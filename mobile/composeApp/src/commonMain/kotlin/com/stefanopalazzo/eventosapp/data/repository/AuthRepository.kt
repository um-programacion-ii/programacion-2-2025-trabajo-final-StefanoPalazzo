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
    
    suspend fun register(username: String, password: String, email: String, firstName: String, lastName: String): Result<Boolean> {
        val request = com.stefanopalazzo.eventosapp.data.models.RegisterRequest(
            username = username,
            password = password,
            email = email,
            firstName = firstName,
            lastName = lastName
        )
        return apiService.register(request).map { response ->
            response.success
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return apiService.logout()
    }

    suspend fun getUserProfile(): Result<com.stefanopalazzo.eventosapp.data.models.UserProfileResponse> {
        return apiService.getUserProfile()
    }
    
    fun isAuthenticated(): Boolean {
        return apiClient.isAuthenticated()
    }
    
    fun getToken(): String? {
        return apiClient.getToken()
    }
}
