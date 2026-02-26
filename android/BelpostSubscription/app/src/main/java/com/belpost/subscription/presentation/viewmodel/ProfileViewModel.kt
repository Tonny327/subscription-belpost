package com.belpost.subscription.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belpost.subscription.data.api.models.SubscriptionResponseDto
import com.belpost.subscription.data.api.models.UserProfileDto
import com.belpost.subscription.data.repository.AuthRepository
import com.belpost.subscription.data.repository.SubscriptionRepository
import com.belpost.subscription.data.repository.UserRepository
import com.belpost.subscription.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userProfileState: MutableStateFlow<UiState<UserProfileDto>> =
        MutableStateFlow(UiState.Loading)
    val userProfileState: StateFlow<UiState<UserProfileDto>> = _userProfileState.asStateFlow()

    private val _subscriptionHistoryState: MutableStateFlow<UiState<List<SubscriptionResponseDto>>> =
        MutableStateFlow(UiState.Loading)
    val subscriptionHistoryState: StateFlow<UiState<List<SubscriptionResponseDto>>> =
        _subscriptionHistoryState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _userProfileState.value = UiState.Loading
            try {
                val profile = userRepository.loadUserProfile()
                _userProfileState.value = UiState.Success(profile)
            } catch (e: Exception) {
                _userProfileState.value = UiState.Error(e.message ?: "Ошибка загрузки профиля")
            }
        }
    }

    fun updateUserProfile(fullName: String, phone: String, email: String) {
        val current = (userProfileState.value as? UiState.Success)?.data
        val updated = UserProfileDto(
            id = current?.id,
            fullName = fullName,
            phone = phone,
            email = email
        )

        viewModelScope.launch {
            try {
                val result = userRepository.updateUserProfile(updated)
                _userProfileState.value = UiState.Success(result)
                _snackbarMessage.value = "Профиль сохранён"
            } catch (e: Exception) {
                _snackbarMessage.value = e.message ?: "Ошибка сохранения профиля"
            }
        }
    }

    fun loadSubscriptionHistory() {
        viewModelScope.launch {
            _subscriptionHistoryState.value = UiState.Loading
            try {
                val history = subscriptionRepository.getMySubscriptions()
                _subscriptionHistoryState.value = UiState.Success(history)
            } catch (e: Exception) {
                _subscriptionHistoryState.value =
                    UiState.Error(e.message ?: "Ошибка загрузки подписок")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun cancelSubscription(subscriptionId: Long) {
        viewModelScope.launch {
            try {
                subscriptionRepository.cancelSubscription(subscriptionId)
                _snackbarMessage.value = "Подписка отменена"
                loadSubscriptionHistory()
            } catch (e: Exception) {
                _snackbarMessage.value = e.message ?: "Ошибка отмены подписки"
            }
        }
    }
}

