package com.belpost.subscription.data.repository

import com.belpost.subscription.data.api.ApiService
import com.belpost.subscription.data.api.models.SubscriptionRequestDto
import com.belpost.subscription.data.api.models.SubscriptionResponseDto

class SubscriptionRepository(
    private val apiService: ApiService
) {

    suspend fun createSubscription(
        request: SubscriptionRequestDto
    ): SubscriptionResponseDto {
        return apiService.createSubscription(request)
    }
}

