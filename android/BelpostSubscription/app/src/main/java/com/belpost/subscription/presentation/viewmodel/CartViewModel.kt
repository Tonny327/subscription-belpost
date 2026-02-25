package com.belpost.subscription.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belpost.subscription.data.api.models.SubscriptionRequestDto
import com.belpost.subscription.data.repository.SubscriptionRepository
import com.belpost.subscription.presentation.cart.CartItem
import com.belpost.subscription.presentation.subscription.SubscriptionPeriod
import com.belpost.subscription.presentation.subscription.calculateSubscriptionPrice
import com.belpost.subscription.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    val totalPrice: StateFlow<Double> = _items
        .map { list -> list.sumOf { it.calculatedPrice } }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
            initialValue = 0.0
        )

    private val _checkoutState = MutableStateFlow<UiState<Unit>?>(null)
    val checkoutState: StateFlow<UiState<Unit>?> = _checkoutState.asStateFlow()

    fun clearCheckoutState() {
        _checkoutState.value = null
    }

    fun addToCart(publication: com.belpost.subscription.data.api.models.PublicationDto, period: SubscriptionPeriod) {
        val price = calculateSubscriptionPrice(publication.price, period)
        _items.update { current ->
            val existingIndex = current.indexOfFirst { it.publication.id == publication.id }
            if (existingIndex >= 0) {
                val updated = current.toMutableList()
                updated[existingIndex] = current[existingIndex].copy(
                    selectedPeriod = period,
                    calculatedPrice = price
                )
                updated
            } else {
                current + CartItem(
                    publication = publication,
                    selectedPeriod = period,
                    calculatedPrice = price
                )
            }
        }
    }

    fun removeFromCart(item: CartItem) {
        _items.update { current -> current.filterNot { it.publication.id == item.publication.id } }
    }

    fun updateItemPeriod(item: CartItem, newPeriod: SubscriptionPeriod) {
        val price = calculateSubscriptionPrice(item.publication.price, newPeriod)
        _items.update { current ->
            current.map {
                if (it.publication.id == item.publication.id) {
                    it.copy(selectedPeriod = newPeriod, calculatedPrice = price)
                } else {
                    it
                }
            }
        }
    }

    fun checkoutAll(
        customerName: String,
        customerPhone: String,
        customerEmail: String
    ) {
        val itemsSnapshot = _items.value
        if (itemsSnapshot.isEmpty()) return

        viewModelScope.launch {
            _checkoutState.value = UiState.Loading
            try {
                for (item in itemsSnapshot) {
                    val request = SubscriptionRequestDto(
                        publicationId = item.publication.id,
                        customerName = customerName,
                        customerPhone = customerPhone,
                        customerEmail = customerEmail,
                        startDate = java.time.LocalDate.now().toString(),
                        period = item.selectedPeriod.label
                    )
                    subscriptionRepository.createSubscription(request)
                }
                _items.value = emptyList()
                _checkoutState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _checkoutState.value = UiState.Error(e.message ?: "Ошибка оформления подписок")
            }
        }
    }
}

