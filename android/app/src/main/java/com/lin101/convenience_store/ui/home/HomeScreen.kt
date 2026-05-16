package com.lin101.convenience_store.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt
import com.lin101.convenience_store.data.model.Store
import com.lin101.convenience_store.ui.store.StoreViewModel
import com.lin101.convenience_store.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val storeViewModel: StoreViewModel = viewModel()

    // 监听来自 ViewModel 的所有状态
    val banners by viewModel.banners.collectAsState()
    val flashSales by viewModel.flashSales.collectAsState()
    val newArrivals by viewModel.newArrivals.collectAsState()
    val shoppingMode by viewModel.shoppingMode.collectAsState()
    val currentStoreName by viewModel.currentStoreName.collectAsState()  // 从 HomeViewModel 读取
    val userAddress by viewModel.userAddress.collectAsState()
    val cartItemCount by viewModel.cartItemCount.collectAsState()

    // 使用 StoreViewModel 中的门店列表和当前门店信息（用于弹窗中的选中状态）
    val stores by storeViewModel.stores.collectAsState()
    val currentStoreId by storeViewModel.currentStoreId.collectAsState()

    // 进入页面时刷新购物车数量，并确保门店列表已加载
    LaunchedEffect(Unit) {
        viewModel.fetchCartCount()
        storeViewModel.loadStores()   // 确保门店列表加载
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // 顶部搜索栏：显示当前门店名称，点击打开底部弹窗
                TopSearchBar(
                    shoppingMode = shoppingMode,
                    locationName = currentStoreName.ifEmpty { "Select Store" },
                    cartCount = cartItemCount,
                    onCartClick = { navController.navigate("cart") },
                    onLocationClick = { showBottomSheet = true }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { PromoBanner(banners, navController) }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { PromoCardsSection(flashSales, navController) }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { SectionTitle("Daily New Arrivals") }
            item { Spacer(modifier = Modifier.height(8.dp)) }

            if (newArrivals.isEmpty()) {
                item {
                    Text("Loading fresh arrivals...", color = Color.Gray, modifier = Modifier.padding(16.dp))
                }
            } else {
                items(newArrivals) { product ->
                    ProductItem(product = product, navController = navController)
                }
            }
        }
    }

    // 底部地址/模式选择弹窗
    if (showBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = LightGray,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
        ) {
            DeliveryModeSelector(
                stores = stores,
                userAddress = userAddress,
                currentMode = shoppingMode,
                currentStoreId = currentStoreId,
                onModeAndStoreSelected = { mode, store ->
                    // 1. 更新购物模式（保存到 UserPreferences 的 shoppingMode 和 currentLocationName）
                    viewModel.updateDeliveryMode(mode, store.storeName)
                    // 2. 更新当前门店（StoreViewModel 会同时更新内存和 DataStore）
                    storeViewModel.selectStore(store.storeId, store.storeName)
                    // 3. 刷新首页数据（因为后端依赖 X-Store-Id）
                    viewModel.fetchHomeData()
                    // 4. 刷新购物车数量（不同门店购物车可能不同）
                    viewModel.fetchCartCount()
                    // 关闭弹窗
                    showBottomSheet = false
                },
                onNavigateToProfile = {
                    showBottomSheet = false
                    navController.navigate("edit_profile")
                }
            )
        }
    }
}

@Composable
fun TopSearchBar(
    shoppingMode: String,
    locationName: String,
    cartCount: Int,
    onCartClick: () -> Unit,
    onLocationClick: () -> Unit
) {
    val prefixText = if (shoppingMode == "pickup") "PICKUP AT" else "DELIVER TO"
    val iconVector = if (shoppingMode == "pickup") Icons.Default.Storefront else Icons.Default.DirectionsCar

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onLocationClick() }
                .padding(vertical = 4.dp)
        ) {
            Text(prefixText, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(iconVector, null, tint = BrandGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(locationName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText, maxLines = 1)
                Icon(Icons.Default.KeyboardArrowDown, "Expand", tint = Color.Gray)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* 搜索逻辑 */ }) {
                Icon(Icons.Default.Search, null, tint = DarkText)
            }
            Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.clickable { onCartClick() }) {
                IconButton(onClick = onCartClick) {
                    Icon(Icons.Default.ShoppingBag, "Cart", tint = DarkText)
                }
                if (cartCount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp, end = 4.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(BrandOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (cartCount > 99) "99+" else cartCount.toString(),
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryModeSelector(
    stores: List<Store>,
    userAddress: String,
    currentMode: String,
    currentStoreId: Int?,
    onModeAndStoreSelected: (String, Store) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    // 默认当前模式如果为空或无效，则使用 "pickup"（到店模式）
    var selectedMode by remember { mutableStateOf(if (currentMode == "shipping") "shipping" else "pickup") }
    var selectedStore by remember { mutableStateOf<Store?>(null) }

    val preselectedStore = if (selectedStore == null && currentStoreId != null) {
        stores.find { it.storeId == currentStoreId }
    } else null

    Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 24.dp)) {
        Text("Service Mode", fontSize = 20.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(20.dp))

        // 模式切换器：顺序改为【到店(Pick Up)】在前，【外卖(Delivery)】在后
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(LightGray)
                .padding(4.dp)
        ) {
            // 顺序：pickup 先，shipping 后
            val modes = listOf("pickup" to "Pick Up", "shipping" to "Delivery")
            modes.forEach { (mode, label) ->
                val isSelected = selectedMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { selectedMode = mode },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) BrandGreen else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 动画显示配送地址区域（仅在外卖模式下）
        AnimatedVisibility(
            visible = selectedMode == "shipping",
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Column {
                Text("Delivery Address", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                if (userAddress.isNotEmpty()) {
                    ModeOptionCard(
                        title = "Home",
                        subtitle = userAddress,
                        icon = Icons.Default.Home,
                        isSelected = true,
                        isWarning = false,
                        onClick = {}
                    )
                } else {
                    ModeOptionCard(
                        title = "No Address Found",
                        subtitle = "Set address in profile",
                        icon = Icons.Default.LocationOff,
                        isSelected = false,
                        isWarning = true,
                        onClick = onNavigateToProfile
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // 门店选择（始终显示，不带动画）
        Text("Select Store", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            stores.forEach { store ->
                val isStoreSelected = selectedStore?.storeId == store.storeId ||
                        (selectedStore == null && preselectedStore?.storeId == store.storeId)
                ModeOptionCard(
                    title = store.storeName,
                    subtitle = store.address,
                    icon = Icons.Default.Storefront,
                    isSelected = isStoreSelected,
                    isWarning = false,
                    onClick = { selectedStore = store }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 确认按钮
        Button(
            onClick = {
                val storeToUse = selectedStore ?: preselectedStore
                storeToUse?.let { store ->
                    onModeAndStoreSelected(selectedMode, store)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            shape = RoundedCornerShape(16.dp),
            enabled = (selectedStore != null || preselectedStore != null)
        ) {
            Text("Confirm", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ModeOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    isWarning: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(
                width = 2.dp,
                color = when {
                    isSelected -> BrandGreen
                    isWarning -> BrandOrange.copy(alpha = 0.5f)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isSelected) BrandGreen.copy(alpha = 0.1f) else LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = when {
                isSelected -> BrandGreen
                isWarning -> BrandOrange
                else -> Color.Gray
            })
        }
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = if (isWarning) BrandOrange else Color.Gray, fontSize = 12.sp)
        }

        when {
            isSelected -> Icon(Icons.Default.CheckCircle, null, tint = BrandGreen)
            isWarning -> Icon(Icons.Default.KeyboardArrowRight, null, tint = BrandOrange)
        }
    }
}

// ================= 以下为原 HomeScreen 中的辅助组件，保持不变 =================

@Composable
private fun PromoBanner(banners: List<com.lin101.convenience_store.data.model.Banner>, navController: NavHostController) {
    if (banners.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 16.dp).clip(RoundedCornerShape(24.dp)).background(DarkGreen)) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center) {
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(BrandGreen).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("PREMIUM SELECTION", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Freshness\nDelivered to\nYour Door", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 28.sp)
                Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = Color.White), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp), modifier = Modifier.height(32.dp).padding(top = 8.dp)) {
                    Text("Order Now", color = DarkGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { banners.size })
    LaunchedEffect(banners.size) {
        while (true) {
            delay(3000)
            if (!pagerState.isScrollInProgress) {
                try { pagerState.animateScrollToPage((pagerState.currentPage + 1) % banners.size) } catch (e: Exception) {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 16.dp).clip(RoundedCornerShape(24.dp))) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            AsyncImage(
                model = banners[page].imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clickable {
                    banners[page].linkUrl?.let { navController.navigate(it) }
                },
                contentScale = ContentScale.Crop
            )
        }
        Row(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(banners.size) { iteration ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (pagerState.currentPage == iteration) BrandGreen else Color.White.copy(alpha = 0.5f))
                )
            }
        }
    }
}

@Composable
private fun PromoCardsSection(flashSales: List<com.lin101.convenience_store.data.model.Product>, navController: NavHostController) {
    val flashProduct = flashSales.firstOrNull()
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(190.dp)
                .shadow(2.dp, RoundedCornerShape(24.dp))
                .background(Color.White)
                .clip(RoundedCornerShape(24.dp))
                .clickable(enabled = flashProduct != null) {
                    flashProduct?.let { navController.navigate("product_detail/${it.productId}") }
                }
        ) {
            if (flashProduct != null) {
                val original = flashProduct.originalPrice ?: flashProduct.price
                val discountPercent = if (original > 0) ((original - flashProduct.price) / original * 100).roundToInt() else 0

                var remainingSeconds by remember(flashProduct) { mutableStateOf(calculateRemainingSeconds(flashProduct.flashSaleEndTime)) }
                LaunchedEffect(flashProduct) {
                    while (remainingSeconds > 0) {
                        delay(1000)
                        remainingSeconds--
                    }
                }
                val hours = (remainingSeconds / 3600).toString().padStart(2, '0')
                val minutes = ((remainingSeconds % 3600) / 60).toString().padStart(2, '0')
                val seconds = (remainingSeconds % 60).toString().padStart(2, '0')

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = BrandOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Flash Sale", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        TimePill(if (remainingSeconds > 0) hours else "00")
                        Text(":", color = BrandOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        TimePill(if (remainingSeconds > 0) minutes else "00")
                        Text(":", color = BrandOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        TimePill(if (remainingSeconds > 0) seconds else "00")
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(LightGray)
                ) {
                    AsyncImage(model = flashProduct.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("-$discountPercent%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandRed)
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Flash Deals", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(190.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(AiLightPurple)
                .clickable { navController.navigate("ai_planner") }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AiDeepPurple, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Planner", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AiDeepPurple)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tailored daily combos", color = AiDeepPurple.copy(alpha = 0.7f), fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AiInnerFrame)
            ) {
                Icon(Icons.Default.Memory, contentDescription = null, tint = AiAccentPurple.copy(alpha = 0.4f), modifier = Modifier.align(Alignment.Center).size(48.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AiAccentPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun TimePill(time: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFFFF7ED))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(time, color = BrandOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionTitle(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkText)
        Text("See all", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ProductItem(product: com.lin101.convenience_store.data.model.Product, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("product_detail/${product.productId}") }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(product.description ?: "Fresh arriving", color = Color.Gray, fontSize = 12.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text("$${product.price}", color = BrandGreen, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }

        Button(
            onClick = { navController.navigate("product_detail/${product.productId}") },
            colors = ButtonDefaults.buttonColors(containerColor = LightGray, contentColor = DarkText),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(36.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun calculateRemainingSeconds(endTimeStr: String?): Long {
    if (endTimeStr.isNullOrEmpty()) return 0L
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getDefault()
        val endDate = format.parse(endTimeStr) ?: return 0L
        val diff = endDate.time - System.currentTimeMillis()
        if (diff > 0) diff / 1000 else 0L
    } catch (e: Exception) { 0L }
}