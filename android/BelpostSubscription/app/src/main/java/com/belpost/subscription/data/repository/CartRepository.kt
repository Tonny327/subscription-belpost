package com.belpost.subscription.data.repository

import com.belpost.subscription.data.api.ApiService
import com.belpost.subscription.data.api.models.AddCartItemRequest
import com.belpost.subscription.data.api.models.CartDto
import com.belpost.subscription.data.api.models.CreateOrGetCartRequest
import com.belpost.subscription.data.local.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

interface CartRepositoryApi {
    suspend fun loadCart(): CartDto
    suspend fun addItem(publicationId: Long, period: String): CartDto
    suspend fun removeItem(itemId: Long): CartDto
}

class CartRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : CartRepositoryApi {

    override suspend fun loadCart(): CartDto = loadCartInternal()

    suspend fun createOrGetCart(): CartDto = withContext(Dispatchers.IO) {
        val userId = sessionManager.getUserId()
        val request = if (userId != null) {
            CreateOrGetCartRequest(userId = userId)
        } else {
            CreateOrGetCartRequest(cartToken = sessionManager.getOrCreateCartToken())
        }
        try {
            val cart = apiService.createOrGetCart(request)
            sessionManager.saveCartId(cart.id)
            cart
        } catch (e: HttpException) {
            if (e.code() == 404) sessionManager.clearSession()
            throw e
        }
    }

    suspend fun getCart(): CartDto? = withContext(Dispatchers.IO) {
        val cartId = sessionManager.getCartId()
        val cartToken = sessionManager.getOrCreateCartToken()
        when {
            cartId != null -> try {
                apiService.getCart(cartId)
            } catch (e: Exception) {
                null
            }
            else -> try {
                apiService.getCartByToken(cartToken)
            } catch (e: Exception) {
                null
            }
        }
    }

    private suspend fun loadCartInternal(): CartDto = withContext(Dispatchers.IO) {
        val cartId = sessionManager.getCartId()
        suspend fun createOrGet(): CartDto {
            val uid = sessionManager.getUserId()
            val request = if (uid != null) CreateOrGetCartRequest(userId = uid)
            else CreateOrGetCartRequest(cartToken = sessionManager.getOrCreateCartToken())
            return try {
                val cart = apiService.createOrGetCart(request)
                sessionManager.saveCartId(cart.id)
                cart
            } catch (e: HttpException) {
                if (e.code() == 404) sessionManager.clearSession()
                throw e
            }
        }
        when {
            cartId != null -> try {
                apiService.getCart(cartId)
            } catch (e: HttpException) {
                if (e.code() == 404) sessionManager.clearSession()
                createOrGet()
            } catch (_: Exception) {
                createOrGet()
            }
            else -> createOrGet()
        }
    }

    override suspend fun addItem(publicationId: Long, period: String): CartDto = withContext(Dispatchers.IO) {
        val cart = createOrGetCart()
        val request = AddCartItemRequest(
            publicationId = publicationId,
            period = period,
            quantity = 1,
            cartId = cart.id
        )
        apiService.addCartItem(request)
    }

    override suspend fun removeItem(itemId: Long): CartDto = withContext(Dispatchers.IO) {
        // Backend возвращает 204 No Content, поэтому после успешного удаления
        // просто перечитываем корзину, чтобы вернуть актуальное состояние.
        apiService.removeCartItem(itemId)
        loadCartInternal()
    }
}
