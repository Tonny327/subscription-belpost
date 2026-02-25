package com.belpost.subscription.presentation.viewmodel

import com.belpost.subscription.data.api.models.CategoryDto

sealed class HelpStep {
    object SelectType : HelpStep()
    data class SelectAudience(
        val type: String
    ) : HelpStep()

    data class SelectTheme(
        val type: String,
        val parentCategory: CategoryDto,
        val childCategories: List<CategoryDto>
    ) : HelpStep()
}

