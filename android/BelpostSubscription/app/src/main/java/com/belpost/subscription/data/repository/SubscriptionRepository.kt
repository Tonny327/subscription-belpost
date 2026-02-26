package com.belpost.subscription.data.repository

import com.belpost.subscription.data.api.ApiService
import com.belpost.subscription.data.api.models.SubscriptionRequestDto
import com.belpost.subscription.data.api.models.SubscriptionResponseDto
import com.belpost.subscription.data.local.SessionManager

interface SubscriptionRepositoryApi {
    suspend fun createSubscription(request: SubscriptionRequestDto): SubscriptionResponseDto
    suspend fun getMySubscriptions(): List<SubscriptionResponseDto>
    suspend fun cancelSubscription(id: Long): SubscriptionResponseDto
}

class SubscriptionRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : SubscriptionRepositoryApi {

    override suspend fun createSubscription(
        request: SubscriptionRequestDto
    ): SubscriptionResponseDto {
        return apiService.createSubscription(request)
    }

    override suspend fun getMySubscriptions(): List<SubscriptionResponseDto> {
        val userId = sessionManager.getUserId()
            ?: throw IllegalStateException("Пользователь не авторизован")
        return apiService.getUserSubscriptions(userId)
    }

    override suspend fun cancelSubscription(id: Long): SubscriptionResponseDto {
        return apiService.cancelSubscription(id)
    }
}
