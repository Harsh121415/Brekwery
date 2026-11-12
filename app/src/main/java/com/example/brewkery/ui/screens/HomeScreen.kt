package com.example.brewkery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.brewkery.data.model.Category
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.remote.RetrofitClient
import com.example.brewkery.data.repository.BrewkeryRepository
import com.example.brewkery.ui.viewmodel.BrewkeryViewModel
import com.example.brewkery.ui.viewmodel.MenuUiState

private val CoffeeBrown = Color(0xFF5B3825)
private val CreamBackground = Color(0xFFFFF9F2)
private val LightBrown = Color(0xFFF2E6D9)

@Composable
fun HomeRoute(
    onProductClick: (Int) -> Unit
) {
    val repository = BrewkeryRepository(RetrofitClient.apiService)

    val viewModel: BrewkeryViewModel = viewModel(
        factory = BrewkeryViewModel.Factory(repository)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onSearchChange = viewModel::updateSearchQuery,
        onCategorySelected = viewModel::selectCategory,
        onRetry = viewModel::loadMenu,
        onProductClick = onProductClick
    )
}

@Composable
fun HomeScreen(
    uiState: MenuUiState,
    onSearchChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onRetry: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    Scaffold(
        containerColor = CreamBackground
    ) { paddingValues ->

        when {
            uiState.isLoading && uiState.items.isEmpty() -> {
                LoadingScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            uiState.error != null && uiState.items.isEmpty() -> {
                ErrorScreen(
                    message = uiState.error,
                    onRetry = onRetry,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(
                        start = 18.dp,
                        end = 18.dp,
                        top = 18.dp,
                        bottom = 28.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        HeaderSection()
                    }

                    item {
                        AnnouncementBanner(
                            tagline = uiState.meta?.tagline
                                ?: "Freshly brewed happiness."
                        )
                    }

                    item {
                        SearchSection(
                            query = uiState.searchQuery,
                            onQueryChange = onSearchChange
                        )
                    }

                    item {
                        CategorySection(
                            categories = uiState.categories,
                            selectedCategoryId = uiState.selectedCategoryId,
                            onCategorySelected = onCategorySelected
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (uiState.searchQuery.isBlank()) {
                                    "Popular Menu"
                                } else {
                                    "Search Results"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = CoffeeBrown
                            )

                            Text(
                                text = "${uiState.filteredItems.size} items",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    if (uiState.isLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = CoffeeBrown
                                )
                            }
                        }
                    }

                    if (uiState.error != null) {
                        item {
                            Text(
                                text = "Refresh failed: ${uiState.error}",
                                color = Color(0xFFB3261E),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    if (uiState.filteredItems.isEmpty()) {
                        item {
                            EmptyMenuScreen()
                        }
                    } else {
                        items(
                            items = uiState.filteredItems,
                            key = { it.id }
                        ) { menuItem ->
                            ProductCard(
                                menuItem = menuItem,
                                onClick = { onProductClick(menuItem.id) }
                            )
                        }
                    }

                    item {
                        DeliveryInfo(
                            deliveryTime = uiState.meta?.estimatedDeliveryTime
                                ?: "20 - 30 mins"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "BREWKERY",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = CoffeeBrown
            )

            Text(
                text = "Coffee. Comfort. Happiness.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = LightBrown
        ) {
            Text(
                text = "☕",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun AnnouncementBanner(tagline: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CoffeeBrown
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "YOUR DAILY COFFEE FIX",
                color = Color(0xFFFFD9A8),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tagline,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Made fresh. Served with love.",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SearchSection(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("Search coffee, tea and more...")
        },
        leadingIcon = {
            Text("🔍")
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun CategorySection(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = CoffeeBrown
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategoryId == "all",
                    onClick = { onCategorySelected("all") },
                    label = { Text("All") }
                )
            }

            items(categories, key = { it.id }) { category ->
                FilterChip(
                    selected = selectedCategoryId == category.id,
                    onClick = {
                        onCategorySelected(category.id)
                    },
                    label = {
                        Text("${category.icon} ${category.name}")
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    menuItem: MenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = menuItem.imageUrl,
                contentDescription = menuItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 112.dp, height = 128.dp)
                    .clip(RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (menuItem.badge.isNotBlank()) {
                    Text(
                        text = menuItem.badge.uppercase(),
                        color = CoffeeBrown,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = menuItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CoffeeBrown,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = menuItem.tagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "★ ${menuItem.rating} (${menuItem.reviewCount} reviews)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFB7791F)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${"%.2f".format(menuItem.basePrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = CoffeeBrown
                    )

                    Text(
                        text = "⏱ ${menuItem.prepTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun DeliveryInfo(deliveryTime: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = LightBrown
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🚚", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "Quick delivery",
                    fontWeight = FontWeight.Bold,
                    color = CoffeeBrown
                )

                Text(
                    text = "Estimated delivery: $deliveryTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = CoffeeBrown)

            Spacer(modifier = Modifier.height(14.dp))

            Text("Brewing your menu...")
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "☕",
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CoffeeBrown
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

@Composable
private fun EmptyMenuScreen() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔍",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "No coffee found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CoffeeBrown
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Try searching for something else or pick another category.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
