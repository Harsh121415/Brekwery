
package com.example.brewkery.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.brewkery.ui.screens.CartScreen
import com.example.brewkery.ui.screens.HomeRoute
import com.example.brewkery.ui.screens.ItemDetailRoute
import com.example.brewkery.ui.screens.OrderStatusScreen
import com.example.brewkery.ui.viewmodel.CartViewModel
import com.example.brewkery.ui.viewmodel.OrderViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Shared ViewModels for the app navigation
    val cartViewModel: CartViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()

    val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val orderState by orderViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeRoute(
                onProductClick = { itemId ->
                    navController.navigate("detail/$itemId")
                }
            )
        }

        composable(
            route = "detail/{itemId}",
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 1

            ItemDetailRoute(
                itemId = itemId,
                onBack = {
                    navController.popBackStack()
                },
                onAddToCart = { item, sizeId, milkId, sugar, price ->

                    cartViewModel.addToCart(
                        menuItem = item,
                        sizeId = sizeId,
                        milkId = milkId,
                        sugarLevel = sugar,
                        unitPrice = price
                    )

                    navController.navigate("cart") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("cart") {
            CartScreen(
                uiState = cartState,

                onIncrease = { index ->
                    cartViewModel.increaseQuantity(index)
                },

                onDecrease = { index ->
                    cartViewModel.decreaseQuantity(index)
                },

                onRemove = { index ->
                    cartViewModel.removeItem(index)
                },

                onCheckout = {
                    if (cartState.items.isNotEmpty()) {
                        orderViewModel.placeOrder(cartState)
                        cartViewModel.clearCart()

                        navController.navigate("order") {
                            launchSingleTop = true
                        }
                    }
                },

                onContinueShopping = {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("order") {
            OrderStatusScreen(
                uiState = orderState,
                onBackToHome = {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
