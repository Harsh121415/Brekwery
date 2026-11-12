
package com.example.brewkery.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.brewkery.data.model.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CartItem(
    val menuItem: MenuItem,
    val sizeId: String,
    val milkId: String,
    val sugarLevel: String,
    val unitPrice: Double,
    val quantity: Int = 1
) {
    val lineTotal: Double
        get() = unitPrice * quantity
}

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val deliveryFee: Double = 2.50,
    val taxRate: Double = 8.0
) {
    val subtotal: Double
        get() = items.sumOf { it.lineTotal }

    val tax: Double
        get() = subtotal * taxRate / 100.0

    val total: Double
        get() = subtotal + deliveryFee + tax

    val totalQuantity: Int
        get() = items.sumOf { it.quantity }
}

class CartViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun addToCart(
        menuItem: MenuItem,
        sizeId: String,
        milkId: String,
        sugarLevel: String,
        unitPrice: Double
    ) {
        _uiState.update { state ->

            val existingItem = state.items.find {
                it.menuItem.id == menuItem.id &&
                        it.sizeId == sizeId &&
                        it.milkId == milkId &&
                        it.sugarLevel == sugarLevel
            }

            if (existingItem != null) {
                state.copy(
                    items = state.items.map { cartItem ->
                        if (cartItem == existingItem) {
                            cartItem.copy(
                                quantity = cartItem.quantity + 1
                            )
                        } else {
                            cartItem
                        }
                    }
                )
            } else {
                state.copy(
                    items = state.items + CartItem(
                        menuItem = menuItem,
                        sizeId = sizeId,
                        milkId = milkId,
                        sugarLevel = sugarLevel,
                        unitPrice = unitPrice
                    )
                )
            }
        }
    }

    fun increaseQuantity(index: Int) {
        _uiState.update { state ->
            state.copy(
                items = state.items.mapIndexed { i, item ->
                    if (i == index) {
                        item.copy(quantity = item.quantity + 1)
                    } else {
                        item
                    }
                }
            )
        }
    }

    fun decreaseQuantity(index: Int) {
        _uiState.update { state ->
            state.copy(
                items = state.items.mapIndexedNotNull { i, item ->
                    if (i != index) {
                        item
                    } else if (item.quantity > 1) {
                        item.copy(quantity = item.quantity - 1)
                    } else {
                        null
                    }
                }
            )
        }
    }

    fun removeItem(index: Int) {
        _uiState.update { state ->
            state.copy(
                items = state.items.filterIndexed { i, _ ->
                    i != index
                }
            )
        }
    }

    fun clearCart() {
        _uiState.value = CartUiState()
    }
}
