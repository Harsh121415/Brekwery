package com.example.brewkery.data.remote

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ApiTest {

    @Test
    fun fetchMenuSuccessfully() = runBlocking {
        val response = RetrofitClient.apiService.getMenu()

        assertNotNull(response)
        assertEquals("Brewkery", response.meta.app)
        assertEquals(3, response.categories.size)
        assertEquals(6, response.items.size)
    }

    @Test
    fun fetchSingleItemSuccessfully() = runBlocking {
        val item = RetrofitClient.apiService.getItemById(1)

        assertEquals(1, item.id)
        assertEquals(
            "Toasted Caramel Macchiato",
            item.name
        )

        assertNotNull(item.customizations)
    }
}
