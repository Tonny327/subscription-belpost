package com.belpost.subscription

import com.belpost.subscription.data.api.ApiService
import com.belpost.subscription.data.api.models.CategoryDto
import com.belpost.subscription.data.api.models.PublicationDto
import com.belpost.subscription.data.api.models.SubscriptionRequestDto
import com.belpost.subscription.data.api.models.SubscriptionResponseDto
import com.belpost.subscription.data.api.models.UserProfileDto
import com.belpost.subscription.data.repository.SubscriptionRepository
import com.belpost.subscription.presentation.subscription.SubscriptionPeriod
import com.belpost.subscription.presentation.viewmodel.CartViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private val fakeApi = object : ApiService {
        override suspend fun getTopCategories(): List<CategoryDto> = emptyList()
        override suspend fun getChildCategories(parentId: Long): List<CategoryDto> = emptyList()
        override suspend fun getPublications(
            type: String?,
            categoryId: Long?
        ): List<PublicationDto> = emptyList()

        override suspend fun createSubscription(request: SubscriptionRequestDto): SubscriptionResponseDto {
            return SubscriptionResponseDto(
                id = 1L,
                publication = PublicationDto(
                    id = request.publicationId,
                    title = "Test",
                    description = null,
                    price = 10.0,
                    period = request.period,
                    type = "MAGAZINE",
                    imageUrl = null,
                    categoryNames = emptySet()
                ),
                customerName = request.customerName,
                customerPhone = request.customerPhone,
                customerEmail = request.customerEmail,
                startDate = request.startDate,
                endDate = request.startDate,
                status = "ACTIVE",
                period = request.period,
                totalPrice = 10.0
            )
        }

        override suspend fun getUser(id: Long): UserProfileDto =
            throw UnsupportedOperationException("Not needed in CartViewModelTest")

        override suspend fun updateUser(id: Long, profile: UserProfileDto): UserProfileDto =
            throw UnsupportedOperationException("Not needed in CartViewModelTest")

        override suspend fun getUserSubscriptions(id: Long): List<SubscriptionResponseDto> = emptyList()

        override suspend fun cancelSubscription(id: Long): SubscriptionResponseDto =
            throw UnsupportedOperationException("Not needed in CartViewModelTest")
    }

    private val repository = SubscriptionRepository(fakeApi)

    @Test
    fun `addToCart calculates price`() = runTest(StandardTestDispatcher()) {
        val viewModel = CartViewModel(repository)
        val publication = PublicationDto(
            id = 1L,
            title = "Test",
            description = null,
            price = 10.0,
            period = "1 месяц",
            type = "MAGAZINE",
            imageUrl = null,
            categoryNames = emptySet()
        )

        viewModel.addToCart(publication, SubscriptionPeriod.THREE_MONTHS)

        val items = viewModel.items.value
        assertEquals(1, items.size)
        assertEquals(27.0, items.first().calculatedPrice, 0.0001)
    }
}

