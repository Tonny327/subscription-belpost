package com.belpost.subscription.data.repository

import com.belpost.subscription.data.api.ApiService
import com.belpost.subscription.data.api.models.CategoryDto

class CategoryRepository(
    private val apiService: ApiService
) {

    suspend fun getTopCategories(): List<CategoryDto> {
        return apiService.getTopCategories()
    }

    suspend fun getChildCategories(parentId: Long): List<CategoryDto> {
        return apiService.getChildCategories(parentId)
    }
}

