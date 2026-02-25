package com.belpost.subscription.data.repository

import com.belpost.subscription.data.api.ApiService
import com.belpost.subscription.data.api.models.UserProfileDto

class UserRepository(
    private val apiService: ApiService
) {

    // TODO: заменить на реальный идентификатор пользователя после интеграции логина
    private val defaultUserId: Long = 1L

    suspend fun loadUserProfile(): UserProfileDto {
        return apiService.getUser(defaultUserId)
    }

    suspend fun updateUserProfile(profile: UserProfileDto): UserProfileDto {
        return apiService.updateUser(defaultUserId, profile)
    }
}

