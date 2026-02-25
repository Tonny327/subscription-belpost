package com.belpost.subscription

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.belpost.subscription.data.api.RetrofitClient
import com.belpost.subscription.data.repository.CategoryRepository
import com.belpost.subscription.data.repository.PublicationRepository
import com.belpost.subscription.data.repository.SubscriptionRepository
import com.belpost.subscription.data.repository.UserRepository
import com.belpost.subscription.presentation.viewmodel.CartViewModel
import com.belpost.subscription.presentation.viewmodel.MainViewModel
import com.belpost.subscription.presentation.viewmodel.ProfileViewModel
import com.belpost.subscription.presentation.viewmodel.SubscriptionViewModel

class AppContainer {

    private val apiService = RetrofitClient.apiService

    private val publicationRepository by lazy { PublicationRepository(apiService) }
    private val categoryRepository by lazy { CategoryRepository(apiService) }
    private val subscriptionRepository by lazy { SubscriptionRepository(apiService) }
    private val userRepository by lazy { UserRepository(apiService) }

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
                return CartViewModel(subscriptionRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    val profileViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(userRepository, subscriptionRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

class BelpostApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer()
    }
}

