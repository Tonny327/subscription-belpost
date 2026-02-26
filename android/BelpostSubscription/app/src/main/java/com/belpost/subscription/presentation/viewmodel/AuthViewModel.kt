package com.belpost.subscription.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belpost.subscription.data.repository.AuthRepository
import com.belpost.subscription.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Unit>?>(null)
    val loginState: StateFlow<UiState<Unit>?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<Unit>?>(null)
    val registerState: StateFlow<UiState<Unit>?> = _registerState.asStateFlow()

    fun clearLoginState() { _loginState.value = null }
    fun clearRegisterState() { _registerState.value = null }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("Заполните все поля")
            return
        }
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                authRepository.login(email, password)
                _loginState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Ошибка входа")
            }
        }
    }

    fun register(fullName: String, email: String, phone: String, password: String) {
        when {
            fullName.isBlank() -> _registerState.value = UiState.Error("Введите ФИО")
            email.isBlank() -> _registerState.value = UiState.Error("Введите email")
            phone.isBlank() -> _registerState.value = UiState.Error("Введите телефон")
            password.isBlank() -> _registerState.value = UiState.Error("Введите пароль")
            password.length < 6 -> _registerState.value = UiState.Error("Пароль должен быть не менее 6 символов")
            else -> {
                viewModelScope.launch {
                    _registerState.value = UiState.Loading
                    try {
                        authRepository.register(fullName, email, phone, password)
                        _registerState.value = UiState.Success(Unit)
                    } catch (e: Exception) {
                        _registerState.value = UiState.Error(e.message ?: "Ошибка регистрации")
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
