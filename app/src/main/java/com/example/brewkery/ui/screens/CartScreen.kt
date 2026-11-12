
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.ui.viewmodel.CartItem
import com.example.brewkery.ui.viewmodel.CartUiState

private val CartBrown = Color(0xFF5B3825)
private val CartCream = Color(0xFFFFF9F2)
private val CartLightBrown = Color(0xFFF2E6D9)

@Composable
fun CartScreen(
    uiState: CartUiState,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onCheckout: () -> Unit,
    onContinueShopping: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CartCream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Cart",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = CartBrown
                )

                Text(
                    text = "${uiState.totalQuantity} items in your cart",
                    color = Color.Gray
                )
            }

            Text(
                text = "☕",
                style = MaterialTheme.typography.headlineLarge
            )
        }

        if (uiState.items.isEmpty()) {
            EmptyCartScreen(
                onContinueShopping = onContinueShopping,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(
                    items = uiState.items,
                    key = { _, item ->
                        "${item.menuItem.id}_${item.sizeId}_${item.milkId}_${item.sugarLevel}"
                    }
                ) { index, cartItem ->
                    CartProductCard(
                        cartItem = cartItem,
                        onIncrease = { onIncrease(index) },
                        onDecrease = { onDecrease(index) },
                        onRemove = { onRemove(index) }
                    )
                }

                item {
                    BillSummary(uiState = uiState)
                }
            }

            CheckoutBar(
                total = uiState.total,
                onCheckout = onCheckout
            )
        }
    }
}

@Composable
private fun CartProductCard(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    val item = cartItem.menuItem

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        color = CartBrown,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Size: ${item.customizations.sizes.find { it.id == cartItem.sizeId }?.label ?: cartItem.sizeId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Text(
                        text = "Milk: ${item.customizations.milkOptions.find { it.id == cartItem.milkId }?.name ?: cartItem.milkId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Text(
                        text = "Sugar: ${cartItem.sugarLevel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Text(
                        text = "\$${"%.2f".format(cartItem.unitPrice)} each",
                        color = CartBrown,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onRemove) {
                    Text(
                        text = "Remove",
                        color = Color(0xFFB3261E)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecrease,
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 0.dp
                        )
                    ) {
                        Text("−")
                    }

                    Text(
                        text = cartItem.quantity.toString(),
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(
                        onClick = onIncrease,
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 0.dp
                        )
                    ) {
                        Text("+")
                    }
                }

                Text(
                    text = "\$${"%.2f".format(cartItem.lineTotal)}",
                    fontWeight = FontWeight.Bold,
                    color = CartBrown
                )
            }
        }
    }
}

@Composable
private fun BillSummary(uiState: CartUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = CartLightBrown
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Bill Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = CartBrown
            )

            BillRow("Subtotal", uiState.subtotal)

            BillRow("Delivery Fee", uiState.deliveryFee)

            BillRow(
                "Tax (${uiState.taxRate}%)",
                uiState.tax
            )

            Divider(color = Color.Gray.copy(alpha = 0.3f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Amount",
                    fontWeight = FontWeight.Bold,
                    color = CartBrown
                )

                Text(
                    text = "\$${"%.2f".format(uiState.total)}",
                    fontWeight = FontWeight.ExtraBold,
                    color = CartBrown,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
private fun BillRow(
    label: String,
    amount: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.DarkGray
        )

        Text(
            text = "\$${"%.2f".format(amount)}",
            color = Color.DarkGray
        )
    }
}

@Composable
private fun CheckoutBar(
    total: Double,
    onCheckout: () -> Unit
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
                    text = "Payable amount",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "\$${"%.2f".format(total)}",
                    color = CartBrown,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Button(
                onClick = onCheckout,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Place Order")
            }
        }
    }
}

@Composable
private fun EmptyCartScreen(
    onContinueShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🛒",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your cart is empty",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = CartBrown
        )

        Text(
            text = "Add something delicious from our menu.",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(onClick = onContinueShopping) {
            Text("Explore Menu")
        }
    }
}
