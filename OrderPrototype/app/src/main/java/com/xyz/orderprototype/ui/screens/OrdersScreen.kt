package com.xyz.orderprototype.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.xyz.orderprototype.data.local.DataStoreManager
import com.xyz.orderprototype.data.local.cache.AppCacheDatabase
import com.xyz.orderprototype.data.local.cache.toCachedOrder
import com.xyz.orderprototype.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen() {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)
    val token by dataStoreManager.tokenFlow.collectAsState(initial = "")
    val scope = rememberCoroutineScope()
    val cacheDao = remember { AppCacheDatabase.getInstance(context).cacheDao() }
    val cachedOrders by cacheDao.observeOrders().collectAsState(initial = emptyList())
    val orders = cachedOrders.map { it.toOrderResponse() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedOrderId by remember { mutableStateOf<String?>(null) }

    fun refreshOrders() {
        val currentToken = token
        if (currentToken.isNullOrBlank()) {
            isLoading = false
            return
        }

        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val freshOrders = withContext(Dispatchers.IO) {
                    RetrofitClient.api.getOrders("Bearer $currentToken")
                }
                withContext(Dispatchers.IO) {
                    cacheDao.saveOrders(freshOrders.map { it.toCachedOrder() })
                }
            } catch (e: Exception) {
                errorMessage = "Could not refresh orders. Showing cached orders."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(token) {
        refreshOrders()
    }

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { refreshOrders() },
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("My Orders", style = MaterialTheme.typography.headlineMedium)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            errorMessage?.let {
                item {
                    Text(it, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { refreshOrders() }) {
                        Text("Retry")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (isLoading && orders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (orders.isEmpty() && !isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No orders yet. Your placed orders will appear here.")
                    }
                }
            }

            items(orders) { order ->
                val isExpanded = expandedOrderId == order.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            expandedOrderId = if (isExpanded) {
                                null
                            } else {
                                order.id
                            }
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Status: ${order.status}")
                        Text("Total: ₹${order.totalAmount}")
                        Text(order.itemNames.joinToString())

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Order ID: ${order.id}")
                            Text("User email: ${order.userEmail}")
                            Text("Restaurant ID: ${order.restaurantId}")
                            Text("Items: ${order.itemNames.joinToString()}")
                            Text("Item count: ${order.itemNames.size}")
                            Text("Amount paid: ₹${order.totalAmount}")
                            Text("Current status: ${order.status}")
                        }
                    }
                }
            }
        }
    }
}
