package com.belpost.subscription.presentation.subscription

enum class SubscriptionPeriod(
    val label: String,
    val coefficient: Double
) {
    ONE_MONTH("1 месяц", 1.0),
    THREE_MONTHS("3 месяца", 2.7),
    SIX_MONTHS("6 месяцев", 5.0),
    ONE_YEAR("1 год", 10.0);

    companion object {
        val all: List<SubscriptionPeriod> = values().toList()

        fun fromLabel(label: String): SubscriptionPeriod? =
            all.firstOrNull { it.label == label }
    }
}

fun calculateSubscriptionPrice(
    basePrice: Double,
    period: SubscriptionPeriod
): Double = basePrice * period.coefficient

