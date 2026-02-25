package com.belpost.subscription.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belpost.subscription.data.api.models.CategoryDto
import com.belpost.subscription.data.api.models.PublicationDto
import com.belpost.subscription.data.repository.CategoryRepository
import com.belpost.subscription.data.repository.PublicationRepository
import com.belpost.subscription.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val publicationRepository: PublicationRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _publicationsState: MutableStateFlow<UiState<List<PublicationDto>>> =
        MutableStateFlow(UiState.Loading)
    val publicationsState: StateFlow<UiState<List<PublicationDto>>> = _publicationsState.asStateFlow()

    private val _helpStep = MutableStateFlow<HelpStep?>(null)
    val helpStep: StateFlow<HelpStep?> = _helpStep.asStateFlow()

    private var selectedType: String? = null
    private var selectedAudienceCategory: CategoryDto? = null
    private var rootCategories: List<CategoryDto> = emptyList()

    private var currentType: String? = null
    private var currentCategoryId: Long? = null

    init {
        loadPublications(type = null, categoryId = null)
    }

    fun loadPublications(type: String?, categoryId: Long?) {
        viewModelScope.launch {
            _publicationsState.value = UiState.Loading
            try {
                currentType = type
                currentCategoryId = categoryId
                val publications = publicationRepository.getPublications(type, categoryId)
                _publicationsState.value = UiState.Success(publications)
            } catch (e: Exception) {
                _publicationsState.value = UiState.Error(e.message ?: "Ошибка загрузки изданий")
            }
        }
    }

    fun refreshPublications() {
        loadPublications(currentType, currentCategoryId)
    }

    fun openHelp() {
        _helpStep.value = HelpStep.SelectType
    }

    fun dismissHelp() {
        _helpStep.value = null
        selectedType = null
        selectedAudienceCategory = null
        rootCategories = emptyList()
    }

    fun selectType(type: String) {
        selectedType = type
        viewModelScope.launch {
            try {
                rootCategories = categoryRepository.getTopCategories()
                _helpStep.value = HelpStep.SelectAudience(type = type)
            } catch (e: Exception) {
                // при ошибке просто закрываем помощник и показываем ошибку загрузки публикаций
                _helpStep.value = null
            }
        }
    }

    private fun findAdultCategory(): CategoryDto? {
        if (rootCategories.isEmpty()) return null
        return rootCategories.firstOrNull { it.name.contains("взрос", ignoreCase = true) }
            ?: rootCategories.firstOrNull()
    }

    private fun findChildCategory(): CategoryDto? {
        if (rootCategories.isEmpty()) return null
        return rootCategories.firstOrNull { it.name.contains("дет", ignoreCase = true) }
            ?: rootCategories.getOrNull(1)
    }

    fun selectAdultAudience() {
        val category = findAdultCategory() ?: return
        selectAudienceCategory(category)
    }

    fun selectChildAudience() {
        val category = findChildCategory() ?: return
        selectAudienceCategory(category)
    }

    fun selectAudienceCategory(category: CategoryDto) {
        selectedAudienceCategory = category
        val type = selectedType ?: return

        viewModelScope.launch {
            try {
                val children = categoryRepository.getChildCategories(category.id)
                _helpStep.value = HelpStep.SelectTheme(
                    type = type,
                    parentCategory = category,
                    childCategories = children
                )
            } catch (e: Exception) {
                _helpStep.value = null
            }
        }
    }

    fun backToType() {
        _helpStep.value = HelpStep.SelectType
    }

    fun backToAudience() {
        val type = selectedType ?: return
        _helpStep.value = HelpStep.SelectAudience(type = type)
    }

    fun selectThemeCategory(category: CategoryDto) {
        val type = selectedType
        if (type != null) {
            loadPublications(type = type, categoryId = category.id)
        } else {
            loadPublications(type = null, categoryId = category.id)
        }
        dismissHelp()
    }
}

