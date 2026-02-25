package com.belpost.subscription.data.repository

import com.belpost.subscription.data.api.ApiService
import com.belpost.subscription.data.api.models.SubscriptionRequestDto
import com.belpost.subscription.data.api.models.SubscriptionResponseDto

class SubscriptionRepository(
    private val apiService: ApiService
) {

    // TODO: заменить на реальный идентификатор пользователя после интеграции логина
    private val defaultUserId: Long = 1L

    suspend fun createSubscription(
        request: SubscriptionRequestDto
    ): SubscriptionResponseDto {
        return apiService.createSubscription(request)
    }

    suspend fun getMySubscriptions(): List<SubscriptionResponseDto> {
        return apiService.getUserSubscriptions(defaultUserId)
    }

    suspend fun cancelSubscription(id: Long): SubscriptionResponseDto {
        return apiService.cancelSubscription(id)
    }
}

