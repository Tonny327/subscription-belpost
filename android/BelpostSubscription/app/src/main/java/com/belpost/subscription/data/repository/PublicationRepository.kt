package com.belpost.subscription.data.repository

import com.belpost.subscription.data.api.ApiService
import com.belpost.subscription.data.api.models.PublicationDto

class PublicationRepository(
    private val apiService: ApiService
) {

    suspend fun getPublications(
        type: String? = null,
        categoryId: Long? = null
    ): List<PublicationDto> {
        return apiService.getPublications(type, categoryId)
    }
}

