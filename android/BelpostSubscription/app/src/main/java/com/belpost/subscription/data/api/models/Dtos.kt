package com.belpost.subscription.data.api.models

import com.google.gson.annotations.SerializedName

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

data class UserRegisterRequest(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String
)

data class UserLoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val id: Long? = null,
    @SerializedName(value = "userId", alternate = ["user_id"]) val userId: Long? = null,
    val token: String? = null,
    val fullName: String? = null,
    val email: String? = null,
    val user: LoginResponseUser? = null
) {
    fun effectiveUserId(): Long = id ?: userId ?: user?.id ?: error("В ответе сервера нет id пользователя. API логина должен возвращать поле id, userId или user.id.")
}

data class LoginResponseUser(val id: Long? = null)

data class CreateOrGetCartRequest(
    val userId: Long? = null,
    val cartToken: String? = null
)

data class AddCartItemRequest(
    val publicationId: Long,
    val period: String,
    val quantity: Int = 1,
    val cartId: Long? = null,
    val cartToken: String? = null
)

data class CartItemDto(
    val id: Long,
    val publicationId: Long,
    val publication: PublicationDto? = null,
    val period: String,
    val quantity: Int,
    val totalPrice: Double
)

data class CartDto(
    val id: Long,
    val userId: Long? = null,
    val cartToken: String? = null,
    val items: List<CartItemDto> = emptyList()
)


