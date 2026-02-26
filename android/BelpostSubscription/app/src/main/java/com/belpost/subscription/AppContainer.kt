package com.belpost.subscription

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.belpost.subscription.data.api.RetrofitClient
import com.belpost.subscription.data.local.SessionManager
import com.belpost.subscription.data.repository.AuthRepository
import com.belpost.subscription.data.repository.CartRepository
import com.belpost.subscription.data.repository.CategoryRepository
import com.belpost.subscription.data.repository.PublicationRepository
import com.belpost.subscription.data.repository.SubscriptionRepository
import com.belpost.subscription.data.repository.UserRepository
import com.belpost.subscription.presentation.viewmodel.AuthViewModel
import com.belpost.subscription.presentation.viewmodel.CartViewModel
import com.belpost.subscription.presentation.viewmodel.MainViewModel
import com.belpost.subscription.presentation.viewmodel.ProfileViewModel
import com.belpost.subscription.presentation.viewmodel.SubscriptionViewModel

class AppContainer(private val application: Application) {

    private val apiService = RetrofitClient.apiService
    private val sessionManager by lazy { SessionManager(application.applicationContext) }

    private val publicationRepository by lazy { PublicationRepository(apiService) }
    private val categoryRepository by lazy { CategoryRepository(apiService) }
    private val authRepository by lazy { AuthRepository(apiService, sessionManager) }
    private val subscriptionRepository by lazy { SubscriptionRepository(apiService, sessionManager) }
    private val userRepository by lazy { UserRepository(apiService, sessionManager) }
    private val cartRepository by lazy { CartRepository(apiService, sessionManager) }

    val mainViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(publicationRepository, categoryRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    val subscriptionViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SubscriptionViewModel::class.java)) {
                return SubscriptionViewModel(subscriptionRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    val cartViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
                return CartViewModel(cartRepository, subscriptionRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    val authViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    val profileViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(userRepository, subscriptionRepository, authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    fun isLoggedInFlow() = authRepository.isLoggedInFlow()
}

class BelpostApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}

