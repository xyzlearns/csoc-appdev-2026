package com.xyz.orderprototype.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.xyz.orderprototype.R
import com.xyz.orderprototype.data.cart.CartManager
import com.xyz.orderprototype.data.local.cache.AppCacheDatabase
import com.xyz.orderprototype.data.local.cache.toCachedMenuItem
import com.xyz.orderprototype.data.local.cache.toCachedRestaurant
import com.xyz.orderprototype.data.model.MenuItem
import com.xyz.orderprototype.data.model.Restaurant
import com.xyz.orderprototype.data.network.RetrofitClient
import com.xyz.orderprototype.navigation.Screen
import com.xyz.orderprototype.ui.components.MenuItemCard
import com.xyz.orderprototype.ui.theme.FoodGreen
import com.xyz.orderprototype.ui.theme.FoodMuted
import com.xyz.orderprototype.ui.theme.FoodSoft
import com.xyz.orderprototype.ui.theme.OrderPrototypeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MenuScreen(
    navController: NavController,
    restaurantId: String
) {
    var restaurant by remember { mutableStateOf<Restaurant?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cacheDao = remember { AppCacheDatabase.getInstance(context).cacheDao() }
    val cachedMenu by cacheDao.observeMenuItems(restaurantId).collectAsState(initial = emptyList())
    val cachedRestaurants by cacheDao.observeRestaurants().collectAsState(initial = emptyList())
    val menuItems = cachedMenu.map { it.toMenuItem() }
    val cartItems by CartManager.cartItems.collectAsState()

    fun refreshMenu() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val freshRestaurant = withContext(Dispatchers.IO) {
                    RetrofitClient.api.getRestaurant(restaurantId)
                }
                val freshMenu = withContext(Dispatchers.IO) {
                    RetrofitClient.api.getMenu(restaurantId)
                }
                restaurant = freshRestaurant
                withContext(Dispatchers.IO) {
                    cacheDao.saveRestaurants(listOf(freshRestaurant.toCachedRestaurant()))
                    cacheDao.saveMenuItems(freshMenu.map { it.toCachedMenuItem() })
                }
            } catch (e: Exception) {
                errorMessage = "Could not refresh this menu. Showing cached items."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(restaurantId, cachedRestaurants) {
        restaurant = cachedRestaurants.firstOrNull { it.id == restaurantId }?.toRestaurant() ?: restaurant
    }

    LaunchedEffect(restaurantId) {
        refreshMenu()
    }

    val currentRestaurant = restaurant
    if (currentRestaurant == null && isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (currentRestaurant == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(errorMessage ?: "Restaurant details are not available offline.")
            Button(onClick = { refreshMenu() }) {
                Text("Retry")
            }
        }
        return
    }

    MenuScreenContent(
        restaurant = currentRestaurant,
        menuItems = menuItems,
        cartCount = cartItems.size,
        isLoading = isLoading && menuItems.isEmpty(),
        errorMessage = errorMessage,
        onRetry = { refreshMenu() },
        onCartClick = { navController.navigate(Screen.Cart.route) }
    )
}

@Composable
fun MenuScreenContent(
    restaurant: Restaurant,
    menuItems: List<MenuItem>,
    cartCount: Int,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf("Recommended") }
    val categories = listOf("Recommended", "Burgers", "Pizza", "Chicken", "Healthy", "Desserts")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (cartCount > 0) {
                ExtendedFloatingActionButton(
                    onClick = onCartClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
                    text = { Text("View Basket ($cartCount)", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    Image(
                        painter = painterResource(getRestaurantImage(restaurant.image)),
                        contentDescription = restaurant.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.24f))
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .align(Alignment.BottomCenter),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(restaurant.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                    Text(restaurant.category, color = FoodMuted, style = MaterialTheme.typography.bodyMedium)
                                }
                                Surface(color = FoodGreen, shape = RoundedCornerShape(10.dp)) {
                                    Text(
                                        text = restaurant.rating.toString(),
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                InfoPill(restaurant.deliveryTime)
                                InfoPill("Rs 40 delivery")
                                InfoPill("1.2 km")
                            }
                        }
                    }
                }
            }

            errorMessage?.let { message ->
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }
            }

            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) }
                        )
                    }
                    item {
                        Button(onClick = onRetry) {
                            Text("Refresh")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "$selectedCategory Items",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (menuItems.isEmpty() && !isLoading) {
                item {
                    Text(
                        text = "No menu items are available for this restaurant yet.",
                        color = FoodMuted,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                items(menuItems) { item ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MenuItemCard(
                            itemName = item.name,
                            description = item.description,
                            price = "₹${item.price.toInt()}",
                            imageRes = getImageRes(item.image),
                            rating = "4.5",
                            isVeg = true,
                            onAddClick = { CartManager.addItem(item) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun InfoPill(text: String) {
    Surface(color = FoodSoft, shape = RoundedCornerShape(50)) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = FoodMuted,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

private fun getImageRes(imageName: String): Int {
    return when (imageName) {
        "chickenburger.jpg" -> R.drawable.chickenburger_thumb
        "pizza.jpg" -> R.drawable.pizza_thumb
        "salad.jpg" -> R.drawable.salad_thumb
        "cupcake.jpg" -> R.drawable.cupcake_thumb
        "chickenfry.jpg" -> R.drawable.chickenfry_thumb
        "burger.jpg" -> R.drawable.burger
        "chickenball.jpg" -> R.drawable.chickenball
        "cake.jpg" -> R.drawable.cake
        "donut.jpg" -> R.drawable.donut
        "italisnpizza.jpg" -> R.drawable.italisnpizza
        "momo.jpg" -> R.drawable.momo
        "noodles.jpg" -> R.drawable.noodles
        "pancake.jpg" -> R.drawable.pancake
        "sandwich.jpg" -> R.drawable.sandwich
        "icecream.jpg" -> R.drawable.icecream
        "sahidal.jpg" -> R.drawable.sahidal
        "seafishcurry.jpg" -> R.drawable.seafishcurry
        "fishfry.jpg" -> R.drawable.fishfry
        "sikhkebab.jpg" -> R.drawable.sikhkebab
        else -> R.drawable.chickenburger_thumb
    }
}

private fun getRestaurantImage(imageName: String): Int {
    return when (imageName) {
        "burgerking.jpg" -> R.drawable.burgerking
        "pizzahut.jpg" -> R.drawable.pizzahut
        "dominos.jpg" -> R.drawable.dominos
        "subway.jpg" -> R.drawable.subway
        "biriyanihouse.jpg" -> R.drawable.biriyanihouse_thumb
        else -> R.drawable.biriyanihouse_thumb
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun MenuScreenPreview() {
    OrderPrototypeTheme {
        MenuScreenContent(
            restaurant = Restaurant(
                id = "",
                name = "Preview Restaurant",
                image = "burgerking.jpg",
                rating = 4.5,
                deliveryTime = "25 mins",
                category = "Fast Food"
            ),
            menuItems = emptyList(),
            cartCount = 0
        )
    }
}
