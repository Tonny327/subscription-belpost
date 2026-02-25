package com.belpost.subscription.data.api.models

data class CategoryDto(
    val id: Long,
    val name: String,
    val parentId: Long? = null
)

data class PublicationDto(
    val id: Long,
    val title: String,
    val description: String?,
    val price: Double,
    val period: String,
    val type: String,
    val imageUrl: String?,
    val categoryNames: Set<String>?
)

data class SubscriptionRequestDto(
    val publicationId: Long,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val startDate: String,
    val period: String
)

data class SubscriptionResponseDto(
    val id: Long,
    val publication: PublicationDto,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val period: String,
    val totalPrice: Double
)

data class UserProfileDto(
    val id: Long?,
    val fullName: String?,
    val phone: String?,
    val email: String?
)


