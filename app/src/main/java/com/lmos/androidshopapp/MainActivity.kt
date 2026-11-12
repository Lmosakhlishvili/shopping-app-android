package com.lmos.androidshopapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lmos.androidshopapp.data.SampleData
import com.lmos.androidshopapp.model.CartItem
import com.lmos.androidshopapp.model.Product
import com.lmos.androidshopapp.ui.theme.AndroidShopAppTheme
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidShopAppTheme {
                ShopApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopApp() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var cartItems by remember { mutableStateOf(listOf<CartItem>()) }
    var showCheckoutDialog by remember { mutableStateOf(false) }

    val filteredProducts = remember(selectedCategory, searchQuery) {
        SampleData.products.filter { product ->
            val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val totalCartCount = cartItems.sumOf { it.quantity }
    val totalCartPrice = cartItems.sumOf { it.product.price * it.quantity }

    fun addToCart(product: Product) {
        cartItems = if (cartItems.any { it.product.id == product.id }) {
            cartItems.map {
                if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
            }
        } else {
            cartItems + CartItem(product, 1)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                SampleData.categories.forEach { category ->
                    NavigationDrawerItem(
                        label = { Text(category) },
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = category
                            selectedTab = 0
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Categories Menu")
                        }
                    },
                    title = { Text("ShopNow", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { selectedTab = 1 }) {
                            BadgedBox(
                                badge = {
                                    if (totalCartCount > 0) {
                                        Badge { Text(totalCartCount.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Shop") },
                        label = { Text("Shop") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (totalCartCount > 0) {
                                        Badge { Text(totalCartCount.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                        },
                        label = { Text("Cart") }
                    )
                }
            }
        ) { innerPadding ->
            if (selectedTab == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search products...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(SampleData.categories) { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                onClick = { selectedProduct = product },
                                onAddToCart = { addToCart(product) }
                            )
                        }
                    }
                }
            } else {
                CartScreen(
                    modifier = Modifier.padding(innerPadding),
                    cartItems = cartItems,
                    totalPrice = totalCartPrice,
                    onIncrement = { id ->
                        cartItems = cartItems.map {
                            if (it.product.id == id) it.copy(quantity = it.quantity + 1) else it
                        }
                    },
                    onDecrement = { id ->
                        cartItems = cartItems.mapNotNull {
                            when {
                                it.product.id != id -> it
                                it.quantity > 1 -> it.copy(quantity = it.quantity - 1)
                                else -> null
                            }
                        }
                    },
                    onCheckout = { showCheckoutDialog = true }
                )
            }
        }
    }

    selectedProduct?.let { product ->
        ModalBottomSheet(onDismissRequest = { selectedProduct = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(product.category.uppercase(), style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(product.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF9900))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${product.rating} (${product.reviewCount} reviews)")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = String.format(Locale.US, "$%.2f", product.price),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(product.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = {
                        addToCart(product)
                        selectedProduct = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add to Cart", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showCheckoutDialog) {
        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            title = { Text("Order Placed") },
            text = {
                Text(
                    String.format(
                        Locale.US,
                        "Thank you! Your order of %d item(s) totaling $%.2f has been confirmed.",
                        totalCartCount,
                        totalCartPrice
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        cartItems = emptyList()
                        showCheckoutDialog = false
                        selectedTab = 0
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.category, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF9900), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${product.rating} (${product.reviewCount})", style = MaterialTheme.typography.bodySmall)
                if (product.fastDelivery) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Fast Delivery", style = MaterialTheme.typography.labelSmall, color = Color(0xFF007185), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format(Locale.US, "$%.2f", product.price),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Add to Cart", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    cartItems: List<CartItem>,
    totalPrice: Double,
    onIncrement: (Int) -> Unit,
    onDecrement: (Int) -> Unit,
    onCheckout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cartItems, key = { it.product.id }) { item ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.product.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(String.format(Locale.US, "$%.2f each", item.product.price), color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onDecrement(item.product.id) }) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }
                                Text(item.quantity.toString(), fontWeight = FontWeight.Bold)
                                IconButton(onClick = { onIncrement(item.product.id) }) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(String.format(Locale.US, "$%.2f", totalPrice), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onCheckout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Proceed to Checkout", fontWeight = FontWeight.Bold)
            }
        }
    }
}
