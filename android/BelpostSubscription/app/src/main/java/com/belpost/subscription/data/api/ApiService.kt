package com.belpost.subscription.data.api

import com.belpost.subscription.data.api.models.CategoryDto
import com.belpost.subscription.data.api.models.PublicationDto
import com.belpost.subscription.data.api.models.SubscriptionRequestDto
import com.belpost.subscription.data.api.models.SubscriptionResponseDto
import retrofit2.http.Body
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
}

