
package com.example.brewkery.data.remote

import com.example.brewkery.data.model.MenuResponse
import com.example.brewkery.data.model.MenuItem
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    // Fetch complete menu and store configuration
    @GET("data.json")
    suspend fun getMenu(): MenuResponse

    // Fetch individual product by ID
    @GET("api/items/{id}.json")
    suspend fun getItemById(
        @Path("id") id: Int
    ): MenuItem
}
