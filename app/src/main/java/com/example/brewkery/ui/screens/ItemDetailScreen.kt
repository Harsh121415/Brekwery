
package com.example.brewkery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.remote.RetrofitClient
import com.example.brewkery.data.repository.BrewkeryRepository
import com.example.brewkery.ui.viewmodel.ItemDetailUiState
import com.example.brewkery.ui.viewmodel.ItemDetailViewModel

private val DetailBrown = Color(0xFF5B3825)
private val DetailCream = Color(0xFFFFF9F2)
private val DetailLightBrown = Color(0xFFF2E6D9)

@Composable
fun ItemDetailRoute(
    itemId: Int,
    onBack: () -> Unit,
    onAddToCart: (
        MenuItem,
        String,
        String,
        String,
        Double
    ) -> Unit
) {
    val repository = BrewkeryRepository(RetrofitClient.apiService)

    val viewModel: ItemDetailViewModel = viewModel(
        key = "item_detail_$itemId",
        factory = ItemDetailViewModel.Factory(repository)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    ItemDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = { viewModel.loadItem(itemId) },
        onSizeSelected = viewModel::selectSize,
        onMilkSelected = viewModel::selectMilk,
        onSugarSelected = viewModel::selectSugar,
        onAddToCart = {
            val item = uiState.item
            if (item != null) {
                onAddToCart(
                    item,
                    uiState.selectedSizeId,
                    uiState.selectedMilkId,
                    uiState.selectedSugarLevel,
                    uiState.totalPrice
                )
            }
        }
    )
}

@Composable
fun ItemDetailScreen(
    uiState: ItemDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onSizeSelected: (String) -> Unit,
    onMilkSelected: (String) -> Unit,
    onSugarSelected: (String) -> Unit,
    onAddToCart: () -> Unit
) {
    Scaffold(
        containerColor = DetailCream,
        bottomBar = {
            if (!uiState.isLoading && uiState.item != null) {
                AddToCartBar(
                    price = uiState.totalPrice,
                    onAddToCart = onAddToCart
                )
            }
        }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = DetailBrown)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading coffee details...")
                    }
                }
            }

            uiState.error != null || uiState.item == null -> {
                ErrorDetailScreen(
                    message = uiState.error ?: "Item not found.",
                    onBack = onBack,
                    onRetry = onRetry,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            else -> {
                val item = uiState.item

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    item {
                        Box {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(270.dp)
                            )

                            Surface(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.TopStart),
                                shape = RoundedCornerShape(50),
                                color = Color.White
                            ) {
                                TextButton(onClick = onBack) {
                                    Text(
                                        text = "← Back",
                                        color = DetailBrown
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (item.badge.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DetailLightBrown
                                ) {
                                    Text(
                                        text = item.badge.uppercase(),
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 6.dp
                                        ),
                                        color = DetailBrown,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = DetailBrown
                            )

                            Text(
                                text = item.tagline,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(18.dp)
                            ) {
                                Text(
                                    text = "★ ${item.rating} (${item.reviewCount} reviews)",
                                    color = Color(0xFFB7791F)
                                )

                                Text(
                                    text = "⏱ ${item.prepTime}",
                                    color = Color.Gray
                                )

                                Text(
                                    text = "${item.calories} kcal",
                                    color = Color.Gray
                                )
                            }

                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )

                            SectionDivider()

                            Text(
                                text = "Ingredients",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = DetailBrown
                            )

                            item.ingredients.forEach { ingredient ->
                                Text(
                                    text = "• $ingredient",
                                    color = Color.DarkGray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            SectionDivider()

                            Text(
                                text = "Customize your drink",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = DetailBrown
                            )

                            Text(
                                text = "Choose your size",
                                fontWeight = FontWeight.SemiBold
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item.customizations.sizes.forEach { size ->
                                    FilterChip(
                                        selected = uiState.selectedSizeId == size.id,
                                        onClick = {
                                            onSizeSelected(size.id)
                                        },
                                        label = {
                                            Text(
                                                text = size.label +
                                                        if (size.extraPrice > 0) {
                                                            " (+\$${"%.2f".format(size.extraPrice)})"
                                                        } else {
                                                            " (Included)"
                                                        }
                                            )
                                        }
                                    )
                                }
                            }

                            SectionDivider()

                            Text(
                                text = "Choose your milk",
                                fontWeight = FontWeight.SemiBold
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item.customizations.milkOptions.forEach { milk ->
                                    FilterChip(
                                        selected = uiState.selectedMilkId == milk.id,
                                        onClick = {
                                            onMilkSelected(milk.id)
                                        },
                                        label = {
                                            Text(
                                                text = milk.name +
                                                        if (milk.extraPrice > 0) {
                                                            " (+\$${"%.2f".format(milk.extraPrice)})"
                                                        } else {
                                                            " (Included)"
                                                        }
                                            )
                                        }
                                    )
                                }
                            }

                            SectionDivider()

                            Text(
                                text = "Sugar level",
                                fontWeight = FontWeight.SemiBold
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item.customizations.sugarLevels.forEach { sugar ->
                                    FilterChip(
                                        selected = uiState.selectedSugarLevel == sugar,
                                        onClick = {
                                            onSugarSelected(sugar)
                                        },
                                        label = {
                                            Text(sugar)
                                        }
                                    )
                                }
                            }

                            SectionDivider()

                            PriceSummary(
                                basePrice = item.basePrice,
                                totalPrice = uiState.totalPrice
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddToCartBar(
    price: Double,
    onAddToCart: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total price",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "\$${"%.2f".format(price)}",
                    color = DetailBrown,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onAddToCart,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add to Cart")
            }
        }
    }
}

@Composable
private fun PriceSummary(
    basePrice: Double,
    totalPrice: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DetailLightBrown
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Base price")
                Text("\$${"%.2f".format(basePrice)}")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Customized price",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "\$${"%.2f".format(totalPrice)}",
                    fontWeight = FontWeight.Bold,
                    color = DetailBrown
                )
            }
        }
    }
}

@Composable
private fun SectionDivider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE8DCCF))
    )
}

@Composable
private fun ErrorDetailScreen(
    message: String,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Unable to load item",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = message, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Try Again")
        }

        TextButton(onClick = onBack) {
            Text("Go Back")
        }
    }
}
