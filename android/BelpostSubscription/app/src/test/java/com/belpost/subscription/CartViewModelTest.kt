package com.belpost.subscription

import com.belpost.subscription.data.api.models.CartDto
import com.belpost.subscription.data.api.models.CartItemDto
import com.belpost.subscription.data.api.models.PublicationDto
import com.belpost.subscription.data.api.models.SubscriptionRequestDto
import com.belpost.subscription.data.api.models.SubscriptionResponseDto
import com.belpost.subscription.data.repository.CartRepositoryApi
import com.belpost.subscription.data.repository.SubscriptionRepositoryApi
import com.belpost.subscription.presentation.subscription.SubscriptionPeriod
import com.belpost.subscription.presentation.viewmodel.CartViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private val testPublication = PublicationDto(
        id = 1L,
        title = "Test",
        description = null,
        price = 10.0,
        period = "1 месяц",
        type = "MAGAZINE",
        imageUrl = null,
        categoryNames = emptySet()
    )

    private val fakeCartRepository = object : CartRepositoryApi {
        override suspend fun loadCart() = CartDto(id = 1L, items = emptyList())
        override suspend fun addItem(publicationId: Long, period: String) = CartDto(
            id = 1L,
            items = listOf(
                CartItemDto(
                    id = 1L,
                    publicationId = publicationId,
                    publication = testPublication,
                    period = period,
                    quantity = 1,
                    totalPrice = 27.0
                )
            )
        )
        override suspend fun removeItem(itemId: Long) = CartDto(id = 1L, items = emptyList())
    }

    private val fakeSubscriptionRepository = object : SubscriptionRepositoryApi {
        override suspend fun createSubscription(request: SubscriptionRequestDto) =
            SubscriptionResponseDto(
                id = 1L,
                publication = testPublication,
                customerName = "",
                customerPhone = "",
                customerEmail = "",
                startDate = "",
                endDate = "",
                status = "ACTIVE",
                period = request.period,
                totalPrice = 27.0
            )
        override suspend fun getMySubscriptions() = emptyList<SubscriptionResponseDto>()
        override suspend fun cancelSubscription(id: Long) = throw UnsupportedOperationException()
    }

    @Test
    fun `addToCart loads cart with item from repository`() = runTest(StandardTestDispatcher()) {
        val viewModel = CartViewModel(fakeCartRepository, fakeSubscriptionRepository)

        viewModel.addToCart(testPublication, SubscriptionPeriod.THREE_MONTHS)
        advanceUntilIdle()

        val items = viewModel.items.value
        assertEquals(1, items.size)
        assertEquals(27.0, items.first().calculatedPrice, 0.0001)
        assertEquals(SubscriptionPeriod.THREE_MONTHS, items.first().selectedPeriod)
    }
}
