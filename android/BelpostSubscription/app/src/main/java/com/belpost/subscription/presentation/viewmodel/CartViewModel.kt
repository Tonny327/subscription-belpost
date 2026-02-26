package com.belpost.subscription.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belpost.subscription.data.api.models.CartItemDto
import com.belpost.subscription.data.api.models.PublicationDto
import com.belpost.subscription.data.api.models.SubscriptionRequestDto
import com.belpost.subscription.data.repository.CartRepositoryApi
import com.belpost.subscription.data.repository.SubscriptionRepositoryApi
import com.belpost.subscription.presentation.cart.CartItem
import com.belpost.subscription.presentation.subscription.SubscriptionPeriod
import com.belpost.subscription.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepositoryApi,
    private val subscriptionRepository: SubscriptionRepositoryApi
) : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

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

    fun clearError() {
        _errorMessage.value = null
    }

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _loadingState.value = true
            _errorMessage.value = null
            try {
                val cart = cartRepository.loadCart()
                _items.value = cart.items.map { it.toCartItem() }
            } catch (e: Exception) {
                _items.value = emptyList()
                _errorMessage.value = e.message ?: "Ошибка загрузки корзины"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun addToCart(publication: PublicationDto, period: SubscriptionPeriod) {
        viewModelScope.launch {
            _loadingState.value = true
            _errorMessage.value = null
            try {
                val cart = cartRepository.addItem(publication.id, period.label)
                _items.value = cart.items.map { it.toCartItem() }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Ошибка добавления в корзину"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun removeFromCart(item: CartItem) {
        viewModelScope.launch {
            _loadingState.value = true
            _errorMessage.value = null
            try {
                val cart = cartRepository.removeItem(item.id)
                _items.value = cart.items.map { it.toCartItem() }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Ошибка удаления из корзины"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun updateItemPeriod(item: CartItem, newPeriod: SubscriptionPeriod) {
        viewModelScope.launch {
            _loadingState.value = true
            _errorMessage.value = null
            try {
                cartRepository.removeItem(item.id)
                val cart = cartRepository.addItem(item.publication.id, newPeriod.label)
                _items.value = cart.items.map { it.toCartItem() }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Ошибка изменения периода"
            } finally {
                _loadingState.value = false
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
                cartRepository.loadCart().items.forEach { cartItem ->
                    cartRepository.removeItem(cartItem.id)
                }
                _items.value = emptyList()
                _checkoutState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _checkoutState.value = UiState.Error(e.message ?: "Ошибка оформления подписок")
            }
        }
    }
}

private fun CartItemDto.toCartItem(): CartItem {
    val periodEnum = SubscriptionPeriod.fromLabel(period) ?: SubscriptionPeriod.ONE_MONTH
    val pub = publication ?: throw IllegalStateException("Cart item must have publication")
    val price = totalPrice / quantity
    return CartItem(
        id = id,
        publication = pub,
        selectedPeriod = periodEnum,
        calculatedPrice = price
    )
}
