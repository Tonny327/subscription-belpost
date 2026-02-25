package com.belpost.subscription

import com.belpost.subscription.presentation.subscription.SubscriptionPeriod
import com.belpost.subscription.presentation.subscription.calculateSubscriptionPrice
import org.junit.Assert.assertEquals
import org.junit.Test

class SubscriptionPeriodTest {

    @Test
    fun `calculate price for different periods`() {
        val base = 10.0

        assertEquals(10.0, calculateSubscriptionPrice(base, SubscriptionPeriod.ONE_MONTH), 0.0001)
        assertEquals(27.0, calculateSubscriptionPrice(base, SubscriptionPeriod.THREE_MONTHS), 0.0001)
        assertEquals(50.0, calculateSubscriptionPrice(base, SubscriptionPeriod.SIX_MONTHS), 0.0001)
        assertEquals(100.0, calculateSubscriptionPrice(base, SubscriptionPeriod.ONE_YEAR), 0.0001)
    }
}

