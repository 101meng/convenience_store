package com.lin101.convenience_store.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.ui.login.LoginScreen
import com.lin101.convenience_store.ui.cart.CartScreen
import com.lin101.convenience_store.ui.category.CategoryScreen
import com.lin101.convenience_store.ui.home.HomeScreen
import com.lin101.convenience_store.ui.order.OrderHistoryScreen
import com.lin101.convenience_store.ui.profile.EditProfileScreen
import com.lin101.convenience_store.ui.product.ProductDetailScreen
import com.lin101.convenience_store.ui.checkout.CheckoutScreen
import com.lin101.convenience_store.ui.profile.ProfileScreen

val BrandGreen = Color(0xFF4ADE80)
val TextGray = Color(0xFF9CA3AF)

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "HOME", Icons.Default.Home)
    object Category : BottomNavItem("category", "CATEGORY", Icons.Default.GridView)
    object Cart : BottomNavItem("cart", "CART", Icons.Default.ShoppingCart)
    object Orders : BottomNavItem("orders", "ORDERS", Icons.Default.Receipt)
    object Profile : BottomNavItem("profile", "PROFILE", Icons.Default.Person)
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val tokenState by userPreferences.tokenFlow.collectAsState(initial = "LOADING")

    var initialRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(tokenState) {
        if (initialRoute == null && tokenState != "LOADING") {
            initialRoute = if (tokenState.isNullOrEmpty()) "login" else "home"
        }
    }

    // 在决定好去哪之前，显示绿色的加载动画
    if (initialRoute == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandGreen)
        }
        return
    }

    // 路由锁定后，开始构建导航树
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Category.route,
        BottomNavItem.Cart.route,
        BottomNavItem.Orders.route,
        BottomNavItem.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                CustomBottomNavigationBar(navController, currentRoute)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = initialRoute!!, // 使用刚才锁定好的起点
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("checkout") { CheckoutScreen(navController) }
            composable("edit_profile") { EditProfileScreen(navController) }

            composable(
                route = "product_detail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: 1
                ProductDetailScreen(navController = navController, productId = productId)
            }

            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Category.route) { CategoryScreen(navController) }
            composable(BottomNavItem.Cart.route) { CartScreen(navController) }
            composable(BottomNavItem.Orders.route) { OrderHistoryScreen(navController = navController) }
            composable(BottomNavItem.Profile.route) { ProfileScreen(navController) }
        }
    }
}

@Composable
fun CustomBottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    val items = listOf(
        BottomNavItem.Home, BottomNavItem.Category, BottomNavItem.Cart,
        BottomNavItem.Orders, BottomNavItem.Profile
    )

    Box(modifier = Modifier.fillMaxWidth().background(Color.Transparent)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .shadow(8.dp)
                .background(Color.White)
                .height(65.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                if (item == BottomNavItem.Cart) {
                    Spacer(modifier = Modifier.width(60.dp))
                } else {
                    val isSelected = currentRoute == item.route
                    BottomNavIcon(item, isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(BottomNavItem.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-15).dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(BrandGreen)
                    .clickable {
                        navController.navigate(BottomNavItem.Cart.route) {
                            popUpTo(BottomNavItem.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
private fun BottomNavIcon(item: BottomNavItem, isSelected: Boolean, onClick: () -> Unit) {
    val color = if (isSelected) BrandGreen else TextGray
    Column(
        modifier = Modifier.clickable(onClick = onClick).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(item.icon, contentDescription = item.title, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(item.title, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}