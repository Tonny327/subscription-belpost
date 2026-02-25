package com.belpost.subscription.presentation.cart

import com.belpost.subscription.data.api.models.PublicationDto
import com.belpost.subscription.presentation.subscription.SubscriptionPeriod

data class CartItem(
    val publication: PublicationDto,
    val selectedPeriod: SubscriptionPeriod,
    val calculatedPrice: Double
)

