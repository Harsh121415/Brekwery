
package com.example.brewkery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.repository.BrewkeryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ItemDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val item: MenuItem? = null,
    val selectedSizeId: String = "",
    val selectedMilkId: String = "",
    val selectedSugarLevel: String = ""
) {
    val totalPrice: Double
        get() {
            val currentItem = item ?: return 0.0

            val sizePrice = currentItem.customizations.sizes
                .find { it.id == selectedSizeId }
                ?.extraPrice ?: 0.0

            val milkPrice = currentItem.customizations.milkOptions
                .find { it.id == selectedMilkId }
                ?.extraPrice ?: 0.0

            val sugarPrice = Regex("""\(\+\$?(\d+(?:\.\d+)?)\)""")
                .find(selectedSugarLevel)
                ?.groupValues?.get(1)
                ?.toDoubleOrNull() ?: 0.0

            return currentItem.basePrice + sizePrice + milkPrice + sugarPrice
        }
}

class ItemDetailViewModel(
    private val repository: BrewkeryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState: StateFlow<ItemDetailUiState> = _uiState.asStateFlow()

    fun loadItem(itemId: Int) {
        viewModelScope.launch {
            _uiState.value = ItemDetailUiState(isLoading = true)

            repository.getItemById(itemId)
                .onSuccess { item ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            item = item,
                            selectedSizeId =
                                item.customizations.sizes.firstOrNull()?.id.orEmpty(),
                            selectedMilkId =
                                item.customizations.milkOptions.firstOrNull()?.id.orEmpty(),
                            selectedSugarLevel =
                                item.customizations.sugarLevels.firstOrNull().orEmpty()
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                                ?: "Unable to load item details."
                        )
                    }
                }
        }
    }

    fun selectSize(sizeId: String) {
        _uiState.update {
            it.copy(selectedSizeId = sizeId)
        }
    }

    fun selectMilk(milkId: String) {
        _uiState.update {
            it.copy(selectedMilkId = milkId)
        }
    }

    fun selectSugar(sugarLevel: String) {
        _uiState.update {
            it.copy(selectedSugarLevel = sugarLevel)
        }
    }

    class Factory(
        private val repository: BrewkeryRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (modelClass.isAssignableFrom(ItemDetailViewModel::class.java)) {
                return ItemDetailViewModel(repository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
