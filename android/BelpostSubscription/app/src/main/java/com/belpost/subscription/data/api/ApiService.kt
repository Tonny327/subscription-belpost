package com.belpost.subscription.data.api

import com.belpost.subscription.data.api.models.AddCartItemRequest
import com.belpost.subscription.data.api.models.CartDto
import com.belpost.subscription.data.api.models.CategoryDto
import com.belpost.subscription.data.api.models.CreateOrGetCartRequest
import com.belpost.subscription.data.api.models.LoginResponse
import com.belpost.subscription.data.api.models.PublicationDto
import com.belpost.subscription.data.api.models.SubscriptionRequestDto
import com.belpost.subscription.data.api.models.SubscriptionResponseDto
import com.belpost.subscription.data.api.models.UserLoginRequest
import com.belpost.subscription.data.api.models.UserProfileDto
import com.belpost.subscription.data.api.models.UserRegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/api/categories/top")
    suspend fun getTopCategories(): List<CategoryDto>

    @GET("/api/categories/{parentId}/children")
    suspend fun getChildCategories(
        @Path("parentId") parentId: Long
    ): List<CategoryDto>

    @GET("/api/publications")
    suspend fun getPublications(
        @Query("type") type: String? = null,
        @Query("categoryId") categoryId: Long? = null
    ): List<PublicationDto>

    @POST("/api/subscriptions")
    suspend fun createSubscription(
        @Body request: SubscriptionRequestDto
    ): SubscriptionResponseDto

    @GET("/api/users/{id}")
    suspend fun getUser(
        @Path("id") id: Long
    ): UserProfileDto

    @POST("/api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body profile: UserProfileDto
    ): UserProfileDto

    @GET("/api/subscriptions/{id}")
    suspend fun getUserSubscriptions(
        @Path("id") id: Long
    ): List<SubscriptionResponseDto>

    @POST("/api/subscriptions/{id}/cancel")
    suspend fun cancelSubscription(
        @Path("id") id: Long
    ): SubscriptionResponseDto

    @POST("/api/users/register")
    suspend fun register(@Body request: UserRegisterRequest): LoginResponse

    @POST("/api/users/login")
    suspend fun login(@Body request: UserLoginRequest): LoginResponse

    @POST("/api/cart")
    suspend fun createOrGetCart(@Body request: CreateOrGetCartRequest): CartDto

    @GET("/api/cart/{id}")
    suspend fun getCart(@Path("id") cartId: Long): CartDto

    @GET("/api/cart/by-token/{cartToken}")
    suspend fun getCartByToken(@Path("cartToken") cartToken: String): CartDto

    @POST("/api/cart/items")
    suspend fun addCartItem(@Body request: AddCartItemRequest): CartDto

    @DELETE("/api/cart/items/{itemId}")
    suspend fun removeCartItem(@Path("itemId") itemId: Long): Response<Unit>
}

