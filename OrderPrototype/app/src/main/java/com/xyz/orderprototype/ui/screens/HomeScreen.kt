package com.xyz.orderprototype.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.xyz.orderprototype.R
import com.xyz.orderprototype.data.local.cache.AppCacheDatabase
import com.xyz.orderprototype.data.local.cache.toCachedRestaurant
import com.xyz.orderprototype.data.model.Restaurant
import com.xyz.orderprototype.data.network.RetrofitClient
import com.xyz.orderprototype.navigation.Screen
import com.xyz.orderprototype.ui.components.RestaurantCard
import com.xyz.orderprototype.ui.theme.FoodMuted
import com.xyz.orderprototype.ui.theme.OrderPrototypeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var sortBy by remember { mutableStateOf("Rating") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cacheDao = remember { AppCacheDatabase.getInstance(context).cacheDao() }
    val restaurants by cacheDao.observeRestaurants()
        .collectAsState(initial = emptyList())

    fun refreshRestaurants() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val fresh = withContext(Dispatchers.IO) {
                    RetrofitClient.api.getRestaurants()
                }
                withContext(Dispatchers.IO) {
                    cacheDao.saveRestaurants(fresh.map { it.toCachedRestaurant() })
                }
            } catch (e: Exception) {
                errorMessage = "Could not refresh restaurants. Showing cached data."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshRestaurants()
    }

    val categories = listOf("All", "Fast Food", "Indian", "Healthy", "Desserts", "Beverages", "Pizza", "Burger")
    val sortOptions = listOf("Rating", "Delivery time")
    val filteredRestaurants = restaurants
        .map { it.toRestaurant() }
        .filter { restaurant ->
            val matchesSearch = searchText.isBlank()
                    || restaurant.name.contains(searchText, ignoreCase = true)
                    || restaurant.category.contains(searchText, ignoreCase = true)
            val matchesCategory = selectedCategory == "All"
                    || restaurant.category.equals(selectedCategory, ignoreCase = true)
                    || restaurant.name.contains(selectedCategory, ignoreCase = true)
            matchesSearch && matchesCategory
        }
        .let { list ->
            if (sortBy == "Delivery time") {
                list.sortedBy { it.deliveryTime.filter(Char::isDigit).toIntOrNull() ?: Int.MAX_VALUE }
            } else {
                list.sortedByDescending { it.rating }
            }
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { refreshRestaurants() },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Deliver to", style = MaterialTheme.typography.labelMedium, color = FoodMuted)
                                Text("Hostel Block, IIT BHU", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("Fast food, fresh meals and desserts", style = MaterialTheme.typography.bodySmall, color = FoodMuted)
                            }

                            IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(55.dp))
                            }
                        }

                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Search restaurant name or category") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (isLoading && restaurants.isEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                errorMessage?.let { message ->
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(message, color = MaterialTheme.colorScheme.error)
                            Button(onClick = { refreshRestaurants() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(categories) { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category) }
                            )
                        }
                    }
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(sortOptions) { option ->
                            FilterChip(
                                selected = sortBy == option,
                                onClick = { sortBy = option },
                                label = { Text("Sort: $option") }
                            )
                        }
                    }
                }

                item {
                    Text("Restaurants near you", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }

                if (filteredRestaurants.isEmpty() && !isLoading) {
                    item {
                        Text(
                            text = if (searchText.isBlank()) "No restaurants are available yet." else "No search results found.",
                            color = FoodMuted
                        )
                    }
                } else {
                    items(filteredRestaurants) { restaurant ->
                        RestaurantCard(
                            restaurantName = restaurant.name,
                            imageRes = getRestaurantImage(restaurant),
                            cuisine = restaurant.category,
                            rating = restaurant.rating.toString(),
                            deliveryTime = restaurant.deliveryTime,
                            onClick = {
                                navController.navigate(Screen.Menu.createRoute(restaurant.id))
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

private fun getRestaurantImage(restaurant: Restaurant): Int {
    return when {
        restaurant.image == "burgerking.jpg" || restaurant.name == "Burger King" -> R.drawable.burgerking
        restaurant.image == "pizzahut.jpg" || restaurant.name == "Pizza Hut" -> R.drawable.pizzahut
        restaurant.image == "dominos.jpg" || restaurant.name == "Domino's" -> R.drawable.dominos
        restaurant.image == "subway.jpg" || restaurant.name == "Subway" -> R.drawable.subway
        restaurant.image == "biriyanihouse.jpg" || restaurant.name == "Biryani House" -> R.drawable.biriyanihouse
        else -> R.drawable.burgerking
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    OrderPrototypeTheme {
        HomeScreen(navController = rememberNavController())
    }
}
