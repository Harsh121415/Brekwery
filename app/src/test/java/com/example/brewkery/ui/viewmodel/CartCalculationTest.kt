package com.example.brewkery.ui.viewmodel

import com.example.brewkery.data.model.Customizations
import com.example.brewkery.data.model.MenuItem
import org.junit.Assert.assertEquals
import org.junit.Test

class CartCalculationTest {

    private fun createMenuItem(): MenuItem {
        return MenuItem(
            id = 1,
            categoryId = "coffee",
            name = "Test Coffee",
            tagline = "Test tagline",
            description = "Test description",
            basePrice = 5.00,
            rating = 4.5,
            reviewCount = 100,
            prepTime = "5 mins",
            calories = 150,
            imageUrl = "https://example.com/coffee.jpg",
            badge = "",
            ingredients = listOf("Coffee", "Milk"),
            customizations = Customizations(
                sizes = emptyList(),
                sugarLevels = emptyList(),
                milkOptions = emptyList()
            )
        )
    }

    @Test
    fun cartCalculatesSubtotalTaxAndTotalCorrectly() {
        val item = createMenuItem()

        val cartState = CartUiState(
            items = listOf(
                CartItem(
                    menuItem = item,
                    sizeId = "",
                    milkId = "",
                    sugarLevel = "",
                    unitPrice = 5.00,
                    quantity = 2
                )
            ),
            deliveryFee = 2.50,
            taxRate = 8.0
        )

        assertEquals(10.00, cartState.subtotal, 0.001)
        assertEquals(0.80, cartState.tax, 0.001)
        assertEquals(13.30, cartState.total, 0.001)
        assertEquals(2, cartState.totalQuantity)
    }

    @Test
    fun emptyCartHasZeroSubtotalAndTax() {
        val cartState = CartUiState()

        assertEquals(0.0, cartState.subtotal, 0.001)
        assertEquals(0.0, cartState.tax, 0.001)
        assertEquals(2.50, cartState.total, 0.001)
        assertEquals(0, cartState.totalQuantity)
    }
}
