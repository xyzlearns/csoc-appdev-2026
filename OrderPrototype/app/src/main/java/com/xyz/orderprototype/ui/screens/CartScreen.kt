package com.xyz.orderprototype.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.xyz.orderprototype.ui.theme.FoodMuted
import com.xyz.orderprototype.ui.theme.FoodOrange
import com.xyz.orderprototype.ui.theme.FoodSoft
import com.xyz.orderprototype.ui.theme.OrderPrototypeTheme
import androidx.compose.runtime.collectAsState
import com.xyz.orderprototype.data.cart.CartManager
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.xyz.orderprototype.data.local.DataStoreManager
import kotlinx.coroutines.launch
import com.xyz.orderprototype.data.model.order.OrderRequest
import com.xyz.orderprototype.data.network.RetrofitClient

private data class CartItemUi(
    val name: String,
    val price: String,
    val quantity: Int
)

@Composable
fun CartScreen(navController: NavController) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)

    val token by dataStoreManager
        .tokenFlow
        .collectAsState(initial = "")
    var couponCode by remember { mutableStateOf("") }
    var appliedCoupon by remember { mutableStateOf<String?>(null) }
    var couponMessage by remember { mutableStateOf<String?>(null) }
    var isEditingAddress by remember { mutableStateOf(false) }
    var addressTitle by remember { mutableStateOf("Hostel Block, IIT BHU") }
    var addressDetails by remember { mutableStateOf("Room 204, Varanasi, Uttar Pradesh 221005") }
    var addressInstructions by remember { mutableStateOf("Leave at the reception if unavailable.") }
    val savedAddressTitle by dataStoreManager.addressTitleFlow.collectAsState(initial = null)
    val savedAddressDetails by dataStoreManager.addressDetailsFlow.collectAsState(initial = null)
    val savedAddressInstructions by dataStoreManager.addressInstructionsFlow.collectAsState(initial = null)
    val cartItems by CartManager.cartItems.collectAsState()
    val scope = rememberCoroutineScope()

    val subtotal =
        cartItems.sumOf {
            it.item.price * it.quantity
        }

    val tax = subtotal * 0.05

    val deliveryFee = 40.0

    val discount =
        if (appliedCoupon == "COUPON2026") {
            minOf(50.0, subtotal)
        } else {
            0.0
        }

    val total =
        subtotal + tax + deliveryFee - discount

    LaunchedEffect(
        savedAddressTitle,
        savedAddressDetails,
        savedAddressInstructions
    ) {
        savedAddressTitle?.let { addressTitle = it }
        savedAddressDetails?.let { addressDetails = it }
        savedAddressInstructions?.let { addressInstructions = it }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Your Cart",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Burger King - arriving in 25 mins",
                style = MaterialTheme.typography.bodyMedium,
                color = FoodMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (cartItems.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Your cart is empty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add items from a restaurant menu to place an order.",
                        color = FoodMuted
                    )
                }
                return@Column
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            itemsHeader("Items added")
                            cartItems.forEach { cartItem ->

                                Text(
                                    text =
                                        "${cartItem.item.name} x${cartItem.quantity}"
                                )
                            }
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsHeader("Apply Coupon")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = couponCode,
                                    onValueChange = { couponCode = it },
                                    placeholder = { Text("Enter promo code") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        val normalizedCode =
                                            couponCode.trim().uppercase()

                                        if (normalizedCode == "COUPON2026") {
                                            appliedCoupon = normalizedCode
                                            couponMessage = "COUPON2026 applied - You saved Rs ${"%.2f".format(minOf(50.0, subtotal))}"
                                            Toast.makeText(
                                                context,
                                                "Promo code applied",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            appliedCoupon = null
                                            couponMessage = "Invalid promo code"
                                            Toast.makeText(
                                                context,
                                                "Invalid promo code",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
                                ) {
                                    Text("Apply")
                                }
                            }

                            couponMessage?.let { message ->
                                Surface(
                                    color = FoodSoft,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = message,
                                        color = if (appliedCoupon == null) MaterialTheme.colorScheme.error else FoodOrange,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                itemsHeader("Delivery to")
                                Button(
                                    onClick = {
                                        isEditingAddress = !isEditingAddress
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (isEditingAddress) "Cancel" else "Change",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                            if (isEditingAddress) {
                                OutlinedTextField(
                                    value = addressTitle,
                                    onValueChange = { addressTitle = it },
                                    label = { Text("Address name") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = addressDetails,
                                    onValueChange = { addressDetails = it },
                                    label = { Text("Full address") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = addressInstructions,
                                    onValueChange = { addressInstructions = it },
                                    label = { Text("Delivery instructions") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Button(
                                    onClick = {
                                        scope.launch {
                                            dataStoreManager.saveAddress(
                                                addressTitle,
                                                addressDetails,
                                                addressInstructions
                                            )
                                            isEditingAddress = false
                                            Toast.makeText(
                                                context,
                                                "Address saved",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Save Address")
                                }
                            } else {
                                Text(
                                    text = addressTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = addressDetails,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FoodMuted
                                )
                                Surface(
                                    color = FoodSoft,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Instructions: $addressInstructions",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsHeader("Bill details")
                            BillingRow(
                                "Subtotal",
                                "Rs ${"%.2f".format(subtotal)}"
                            )

                            BillingRow(
                                "Taxes",
                                "Rs ${"%.2f".format(tax)}"
                            )

                            BillingRow(
                                "Delivery Fee",
                                "Rs ${"%.2f".format(deliveryFee)}"
                            )

                            if (discount > 0.0) {
                                BillingRow(
                                    "Promo discount",
                                    "- Rs ${"%.2f".format(discount)}",
                                    valueColor = FoodOrange
                                )
                            }

                            BillingRow(
                                "Total",
                                "Rs ${"%.2f".format(total)}",
                                isTotal = true
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            BillingRow("Total", "Rs ${"%.2f".format(total)}", MaterialTheme.colorScheme.onSurface, true)
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rs ${"%.2f".format(total)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Total amount",
                            style = MaterialTheme.typography.bodySmall,
                            color = FoodMuted
                        )
                    }

                    Button(
                        onClick = {

                            scope.launch {
                                if (cartItems.isEmpty()) {
                                    return@launch
                                }

                                try {

                                    RetrofitClient.api.placeOrder(
                                        "Bearer $token",
                                        OrderRequest(
                                            restaurantId =
                                                CartManager.getRestaurantId()
                                                    ?: "",

                                            itemNames =
                                                cartItems.map {
                                                    it.item.name
                                                },

                                            totalAmount = total
                                        )
                                    )

                                    Toast.makeText(
                                        context,
                                        "Order placed successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    CartManager.clearCart()

                                    navController.popBackStack()

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(
                            horizontal = 24.dp,
                            vertical = 12.dp
                        )
                    ) {
                        Text(
                            text = "Place Order",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun itemsHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun CartItemRow(item: CartItemUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Quantity: ${item.quantity}",
                style = MaterialTheme.typography.bodySmall,
                color = FoodMuted
            )
        }

        Text(
            text = item.price,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BillingRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.onSurface else FoodMuted
        )
        Text(
            text = value,
            color = valueColor,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)

@Composable
fun CartScreenPreview(){
    OrderPrototypeTheme() {
        CartScreen(rememberNavController())
    }
}
