package com.belpost.subscription.data.repository

import com.belpost.subscription.data.api.ApiService
import com.belpost.subscription.data.api.models.UserProfileDto
import com.belpost.subscription.data.local.SessionManager

class UserRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    suspend fun loadUserProfile(): UserProfileDto {
        val userId = sessionManager.getUserId()
            ?: throw IllegalStateException("Пользователь не авторизован")
        return apiService.getUser(userId)
    }

    suspend fun updateUserProfile(profile: UserProfileDto): UserProfileDto {
        val userId = sessionManager.getUserId()
            ?: throw IllegalStateException("Пользователь не авторизован")
        return apiService.updateUser(userId, profile)
    }
}
