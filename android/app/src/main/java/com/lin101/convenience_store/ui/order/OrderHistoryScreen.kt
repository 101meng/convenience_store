package com.lin101.convenience_store.ui.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.lin101.convenience_store.data.model.OrderModels
import java.util.Locale

// ================= Dribbble 级极简色板 (严格还原设计图) =================
private val BgOffWhite = Color(0xFFF7F8FA)       // 极简浅灰背景，用于托起纯白卡片
private val CardWhite = Color(0xFFFFFFFF)        // 卡片纯白
private val TextMain = Color(0xFF1C1C1E)         // iOS 级极黑标题
private val TextMuted = Color(0xFF8E8E93)        // 高级次级灰
private val DividerColor = Color(0xFFF0F2F5)     // 极浅分割线

// 状态色板
private val BrandGreen = Color(0xFF34C759)       // 品牌绿 (Completed)
private val BrandBlue = Color(0xFF007AFF)        // 品牌蓝 (Delivering)
private val BrandRed = Color(0xFFFF3B30)         // 品牌红 (Cancelled)
private val BrandOrange = Color(0xFFFF9500)      // 亮橙色 (Pending)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavController, viewModel: OrderHistoryViewModel = viewModel()) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 0: All Orders, 1: Ongoing, 2: Past
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val filteredOrders = remember(orders, selectedTabIndex) {
        orders.filter { order ->
            val status = order.status?.lowercase() ?: ""
            when (selectedTabIndex) {
                0 -> true // All
                1 -> status in listOf("pending", "delivering") // Ongoing
                2 -> status in listOf("completed", "cancelled") // Past
                else -> true
            }
        }
    }

    Scaffold(
        containerColor = BgOffWhite,
        topBar = {
            Column(modifier = Modifier.background(BgOffWhite)) {
                // 1. iOS 风格 TopBar
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Order History",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = TextMain
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextMain)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: 搜索页 */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMain)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgOffWhite)
                )

                // 2. 完美的药丸形 Segmented Control
                CustomSegmentedControl(
                    tabs = listOf("All Orders", "Ongoing", "Past"),
                    selectedIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = BrandGreen)
            } else if (filteredOrders.isEmpty()) {
                EmptyStateView()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp) // Bento Box 之间的巨大呼吸感
                ) {
                    items(filteredOrders) { order ->
                        BentoOrderCard(
                            order = order,
                            onActionClick = {
                                val statusStr = order.status?.lowercase()
                                if (statusStr == "pending") viewModel.payOrder(order.orderId)
                                else if (statusStr == "delivering") viewModel.receiveOrder(order.orderId)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 完美的 iOS 药丸形分段选择器
 */
@Composable
fun CustomSegmentedControl(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(48.dp)
            .background(Color(0xFFEFEFF4), RoundedCornerShape(24.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedIndex == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) CardWhite else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onTabSelected(index) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp,
                    color = if (isSelected) TextMain else TextMuted
                )
            }
        }
    }
}

/**
 * 核心组件：Bento Box 极致视觉悬浮卡片
 */
@Composable
fun BentoOrderCard(
    order: OrderModels.OrderVO,
    onActionClick: () -> Unit
) {
    val status = order.status?.lowercase() ?: "unknown"

    // 动态决定金额颜色：成功/进行中高亮显示，取消则置灰
    val priceColor = when (status) {
        "completed", "delivering", "pending" -> BrandGreen
        else -> TextMuted
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = Color.Black.copy(alpha = 0.04f), // 极柔和的弥散阴影
                ambientColor = Color.Transparent
            )
            .clip(RoundedCornerShape(28.dp))
            .background(CardWhite)
            .padding(24.dp)
    ) {
        Column {
            // ================= 1. 头部：订单号与时间、状态 =================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "#${order.orderSn ?: "ORD-UNKNOWN"}",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = TextMain
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = order.createdAt ?: "Recently",
                        fontSize = 14.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                StatusBadge(status)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ================= 2. 中间：圆形画廊展示商品 =================
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 最多展示前 3 个商品头像
                val displayItems = order.items.take(3)
                val extraCount = order.items.size - 3

                items(displayItems) { item ->
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape) // 纯圆形画廊
                            .background(BgOffWhite)
                    )
                }

                // 如果超出 3 个，显示圆形的 "+X"
                if (extraCount > 0) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5E5EA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+$extraCount",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))

            // ================= 3. 底部：动态金额与行为按钮 =================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL PRICE",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "$%.2f", order.actualAmount),
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = priceColor // 金额颜色跟随订单状态
                    )
                }

                ActionButton(status, onActionClick)
            }
        }
    }
}

/**
 * 极致清新的高对比度状态标签 (10%透明底 + 纯色字)
 */
@Composable
fun StatusBadge(status: String) {
    val (textColor, bgColor) = when (status) {
        "completed" -> BrandGreen to BrandGreen.copy(alpha = 0.12f)
        "delivering" -> BrandBlue to BrandBlue.copy(alpha = 0.12f)
        "cancelled" -> BrandRed to BrandRed.copy(alpha = 0.12f)
        "pending" -> BrandOrange to BrandOrange.copy(alpha = 0.12f)
        else -> TextMuted to BgOffWhite
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.uppercase(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * 完美还原视觉图逻辑的动态操作按钮
 */
@Composable
fun ActionButton(status: String, onClick: () -> Unit) {
    when (status) {
        "completed" -> {
            // 绿色实心按钮
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = CardWhite),
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("Reorder", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        "delivering" -> {
            // 绿色空心描边按钮
            OutlinedButton(
                onClick = onClick,
                shape = CircleShape,
                border = BorderStroke(1.5.dp, BrandGreen),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandGreen),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 0.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("Track", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        "pending" -> {
            // 亮橙色实心按钮
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange, contentColor = CardWhite),
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("Pay Now", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        else -> { // cancelled 或其他
            // 浅灰色实心按钮
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = BgOffWhite, contentColor = TextMuted),
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("View Details", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(120.dp).background(CardWhite, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🛒", fontSize = 48.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("No orders yet", fontWeight = FontWeight.Black, fontSize = 20.sp, color = TextMain)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Your future favorites will appear here", color = TextMuted, fontSize = 14.sp)
    }
}