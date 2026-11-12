
package com.example.brewkery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.brewkery.data.model.Category
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.StoreMeta
import com.example.brewkery.data.remote.RetrofitClient
import com.example.brewkery.data.repository.BrewkeryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MenuUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val meta: StoreMeta? = null,
    val categories: List<Category> = emptyList(),
    val items: List<MenuItem> = emptyList(),
    val selectedCategoryId: String = "all",
    val searchQuery: String = ""
) {
    val filteredItems: List<MenuItem>
        get() {
            return items.filter { item ->
                val matchesCategory =
                    selectedCategoryId == "all" ||
                            item.categoryId == selectedCategoryId

                val query = searchQuery.trim()

                val matchesSearch =
                    query.isEmpty() ||
                            item.name.contains(query, ignoreCase = true) ||
                            item.tagline.contains(query, ignoreCase = true) ||
                            item.description.contains(query, ignoreCase = true)

                matchesCategory && matchesSearch
            }
        }
}

class BrewkeryViewModel(
    private val repository: BrewkeryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        loadMenu()
    }

    fun loadMenu() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }

            repository.getMenu()
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            meta = response.meta,
                            categories = response.categories,
                            items = response.items
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                                ?: "Unable to load menu. Please try again."
                        )
                    }
                }
        }
    }

    fun selectCategory(categoryId: String) {
        _uiState.update {
            it.copy(selectedCategoryId = categoryId)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(searchQuery = query)
        }
    }

    class Factory(
        private val repository: BrewkeryRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (modelClass.isAssignableFrom(BrewkeryViewModel::class.java)) {
                return BrewkeryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
