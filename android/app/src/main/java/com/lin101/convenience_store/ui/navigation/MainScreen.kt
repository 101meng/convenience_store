package com.lin101.convenience_store.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType // 引入用于定义路由参数类型
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument // 引入用于解析路由参数
import com.lin101.convenience_store.ui.login.LoginScreen
import com.lin101.convenience_store.ui.cart.CartScreen
import com.lin101.convenience_store.ui.category.CategoryScreen
import com.lin101.convenience_store.ui.home.HomeScreen
import com.lin101.convenience_store.ui.order.OrderHistoryScreen
import com.lin101.convenience_store.ui.profile.EditProfileScreen
import com.lin101.convenience_store.ui.product.ProductDetailScreen
import com.lin101.convenience_store.ui.checkout.CheckoutScreen
import com.lin101.convenience_store.ui.profile.ProfileScreen

// 全局主题色定义
val BrandGreen = Color(0xFF4ADE80)
val TextGray = Color(0xFF9CA3AF)

/**
 * 底部导航栏的密封类定义
 * 包含路由地址、标题文本和对应的图标
 */
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "HOME", Icons.Default.Home)
    object Category : BottomNavItem("category", "CATEGORY", Icons.Default.GridView)
    object Cart : BottomNavItem("cart", "CART", Icons.Default.ShoppingCart)
    object Orders : BottomNavItem("orders", "ORDERS", Icons.Default.Receipt)
    object Profile : BottomNavItem("profile", "PROFILE", Icons.Default.Person)
}

/**
 * App 的全局主骨架和路由控制器
 * @param startDestination 初始化应用时的默认起始页面
 */
@Composable
fun MainScreen(startDestination: String = "login") {
    // 记住并管理全局的导航控制器
    val navController = rememberNavController()
    // 监听当前路由栈的变化，以决定何时显示底部导航栏
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 定义哪些页面需要显示底部导航栏
    val bottomBarRoutes = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Category.route,
        BottomNavItem.Cart.route,
        BottomNavItem.Orders.route,
        BottomNavItem.Profile.route
    )

    Scaffold(
        bottomBar = {
            // 只有当前路由在 bottomBarRoutes 列表中时，才渲染底部导航栏
            if (currentRoute in bottomBarRoutes) {
                CustomBottomNavigationBar(navController, currentRoute)
            }
        }
    ) { paddingValues ->
        // 导航宿主，配置所有页面的路由规则
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // 无底部导航栏的独立页面
            composable("login") { LoginScreen(navController) }
            composable("checkout") { CheckoutScreen(navController) }
            composable("edit_profile") { EditProfileScreen(navController) }

            // 带有动态参数的商品详情页路由
            // 路由格式为 "product_detail/{productId}"，并限制传入参数必须为 Int 类型
            composable(
                route = "product_detail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                // 解析路由中的 productId 参数，如果解析失败则默认传入 1 作为兜底
                val productId = backStackEntry.arguments?.getInt("productId") ?: 1
                ProductDetailScreen(navController = navController, productId = productId)
            }

            // 底部导航栏对应的五个一级主页面
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Category.route) { CategoryScreen(navController) }
            composable(BottomNavItem.Cart.route) { CartScreen(navController) }
            composable(BottomNavItem.Orders.route) { OrderHistoryScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen(navController) }
        }
    }
}

/**
 * 自定义带中间凸起按钮的底部导航栏
 */
@Composable
fun CustomBottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    // 导航栏的五个 Item 列表
    val items = listOf(
        BottomNavItem.Home, BottomNavItem.Category, BottomNavItem.Cart,
        BottomNavItem.Orders, BottomNavItem.Profile
    )

    Box(modifier = Modifier.fillMaxWidth().background(Color.Transparent)) {
        // 底部白色常规导航栏
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
                // 为中间的购物车按钮留出空白占位区域
                if (item == BottomNavItem.Cart) {
                    Spacer(modifier = Modifier.width(60.dp))
                } else {
                    val isSelected = currentRoute == item.route
                    BottomNavIcon(item, isSelected) {
                        // 点击常规图标的路由跳转逻辑
                        navController.navigate(item.route) {
                            // 强制回退栈到 Home，防止不断叠加新页面导致返回栈过深
                            popUpTo(BottomNavItem.Home.route) { saveState = true }
                            // 避免多次点击同一标签产生多个实例
                            launchSingleTop = true
                            // 恢复之前的状态（如列表滑动位置）
                            restoreState = true
                        }
                    }
                }
            }
        }

        // 绝对定位的中间凸起购物车按钮
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-15).dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp)
        ) {
            // 内部绿色的圆形可点击区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(BrandGreen)
                    .clickable {
                        // 点击购物车按钮的路由跳转逻辑
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

/**
 * 底部导航栏的基础小图标组件
 */
@Composable
private fun BottomNavIcon(item: BottomNavItem, isSelected: Boolean, onClick: () -> Unit) {
    // 根据是否选中动态切换颜色
    val color = if (isSelected) BrandGreen else TextGray
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(item.icon, contentDescription = item.title, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(item.title, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}