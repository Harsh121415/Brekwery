package com.example.brewkery.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.brewkery.data.model.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OrderUiState(
    val ticketId: String = "",
    val status: String = "PREPARING",
    val totalAmount: Double = 0.0,
    val totalItems: Int = 0,
    val orderedItems: List<CartItem> = emptyList(),
    val estimatedDeliveryTime: String = "20 - 30 mins",
    val isOrderPlaced: Boolean = false
)

class OrderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    fun placeOrder(cartState: CartUiState) {
        if (cartState.items.isEmpty()) return

        val ticketId = "BRW-${System.currentTimeMillis().toString().takeLast(6)}"

        _uiState.value = OrderUiState(
            ticketId = ticketId,
            status = "PREPARING",
            totalAmount = cartState.total,
            totalItems = cartState.totalQuantity,
            orderedItems = cartState.items,
            isOrderPlaced = true
        )
    }
}
