package com.belpost.subscription.data.repository

import com.belpost.subscription.data.api.ApiService
import com.belpost.subscription.data.api.models.LoginResponse
import com.belpost.subscription.data.api.models.UserLoginRequest
import com.belpost.subscription.data.api.models.UserRegisterRequest
import com.belpost.subscription.data.local.SessionManager

class AuthRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    suspend fun register(fullName: String, email: String, phone: String, password: String): LoginResponse {
        val request = UserRegisterRequest(
            fullName = fullName,
            email = email,
            phone = phone,
            password = password
        )
        val response = apiService.register(request)
        sessionManager.saveSession(response.effectiveUserId(), response.token)
        return response
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val request = UserLoginRequest(email = email, password = password)
        val response = apiService.login(request)
        sessionManager.saveSession(response.effectiveUserId(), response.token)
        return response
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    fun isLoggedInFlow() = sessionManager.isLoggedInFlow
}
