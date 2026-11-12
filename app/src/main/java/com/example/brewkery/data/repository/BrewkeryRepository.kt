
package com.example.brewkery.data.repository

import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.MenuResponse
import com.example.brewkery.data.remote.ApiService

class BrewkeryRepository(
    private val apiService: ApiService
) {

    suspend fun getMenu(): Result<MenuResponse> {
        return try {
            val response = apiService.getMenu()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItemById(
        id: Int
    ): Result<MenuItem> {
        return try {
            val response = apiService.getItemById(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
