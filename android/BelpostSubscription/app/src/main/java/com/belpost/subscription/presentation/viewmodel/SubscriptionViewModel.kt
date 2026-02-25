package com.belpost.subscription.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belpost.subscription.data.api.models.SubscriptionRequestDto
import com.belpost.subscription.data.api.models.SubscriptionResponseDto
import com.belpost.subscription.data.repository.SubscriptionRepository
import com.belpost.subscription.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubscriptionViewModel(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _subscriptionState: MutableStateFlow<UiState<SubscriptionResponseDto>> =
        MutableStateFlow(UiState.Loading)
    val subscriptionState: StateFlow<UiState<SubscriptionResponseDto>> = _subscriptionState.asStateFlow()

    fun createSubscription(request: SubscriptionRequestDto) {
        viewModelScope.launch {
            _subscriptionState.value = UiState.Loading
            try {
                val response = subscriptionRepository.createSubscription(request)
                _subscriptionState.value = UiState.Success(response)
            } catch (e: Exception) {
                _subscriptionState.value = UiState.Error(e.message ?: "Ошибка оформления подписки")
            }
        }
    }
}

