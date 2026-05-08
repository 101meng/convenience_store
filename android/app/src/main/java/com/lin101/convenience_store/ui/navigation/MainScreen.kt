package com.lin101.convenience_store.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.model.CartItem
import com.lin101.convenience_store.ui.ai.AiDietitianScreen
import com.lin101.convenience_store.ui.ai.AiPlannerScreen
import com.lin101.convenience_store.ui.cart.CartScreen
import com.lin101.convenience_store.ui.category.CategoryScreen
import com.lin101.convenience_store.ui.checkout.CheckoutScreen
import com.lin101.convenience_store.ui.home.HomeScreen
import com.lin101.convenience_store.ui.login.LoginScreen
import com.lin101.convenience_store.ui.order.OrderHistoryScreen
import com.lin101.convenience_store.ui.product.ProductDetailScreen
import com.lin101.convenience_store.ui.profile.EditProfileScreen
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

    if (initialRoute == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandGreen)
        }
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("/")

    val bottomBarRoutes = listOf(
        BottomNavItem.Home.route, BottomNavItem.Category.route, BottomNavItem.Cart.route,
        BottomNavItem.Orders.route, BottomNavItem.Profile.route
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
            startDestination = initialRoute!!,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("checkout") { CheckoutScreen(navController) }
            composable("edit_profile") { EditProfileScreen(navController) }

            // 之前的 AI 场景搭配页面
            composable("ai_planner") { AiPlannerScreen(navController) }

            // ==========================================
            // 【新增】：接收 JSON 参数的 AI 营养雷达页面路由
            // ==========================================
            composable(
                route = "ai_dietitian/{cartJson}",
                arguments = listOf(navArgument("cartJson") { type = NavType.StringType })
            ) { backStackEntry ->
                // 取出 JSON 字符串
                val cartJson = backStackEntry.arguments?.getString("cartJson") ?: "[]"
                // 转换回 List<CartItem>
                val listType = object : TypeToken<List<CartItem>>() {}.type
                val cartItems: List<CartItem> = Gson().fromJson(cartJson, listType)

                // 启动页面并传递数据
                AiDietitianScreen(navController = navController, cartItems = cartItems)
            }

            composable(
                route = "product_detail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: 1
                ProductDetailScreen(navController = navController, productId = productId)
            }

            composable(BottomNavItem.Home.route) { HomeScreen(navController = navController) }
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
            modifier = Modifier.fillMaxWidth(),
            // 移除 Arrangement.SpaceAround，改用权重控制
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                if (item == BottomNavItem.Cart) {
                    // 中间给购物车留出的位置也占 1/5 权重
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    BottomNavIcon(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.weight(1f) // 👈 核心：每个图标强行平分 1/5 宽度
                    )
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
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Cart",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun BottomNavIcon(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // 👈 1. 新增 modifier 参数
) {
    val color = if (isSelected) BrandGreen else TextGray
    Column(
        modifier = modifier // 👈 2. 使用传入的 modifier (包含 weight)
            .clickable(onClick = onClick) // 👈 3. 点击事件在前，确保覆盖整个 weight 区域
            .padding(vertical = 12.dp),   // 👈 4. 只留垂直边距，水平方向充满
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            item.icon,
            contentDescription = item.title,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(item.title, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}