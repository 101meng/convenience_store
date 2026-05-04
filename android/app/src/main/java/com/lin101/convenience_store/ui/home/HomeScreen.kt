package com.lin101.convenience_store.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

import com.lin101.convenience_store.ui.theme.AiAccentPurple
import com.lin101.convenience_store.ui.theme.AiDeepPurple
import com.lin101.convenience_store.ui.theme.AiInnerFrame
import com.lin101.convenience_store.ui.theme.AiLightPurple
import com.lin101.convenience_store.ui.theme.BrandGreen
import com.lin101.convenience_store.ui.theme.BrandOrange
import com.lin101.convenience_store.ui.theme.BrandRed
import com.lin101.convenience_store.ui.theme.DarkGreen
import com.lin101.convenience_store.ui.theme.DarkText
import com.lin101.convenience_store.ui.theme.LightGray

/**
 * 首页主界面
 * 包含：顶部搜索栏、轮播Banner、限时秒杀、AI推荐、每日新品列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    // 页面数据状态
    val banners by viewModel.banners.collectAsState()
    val flashSales by viewModel.flashSales.collectAsState()
    val newArrivals by viewModel.newArrivals.collectAsState()

    // 购物模式与位置信息
    val shoppingMode by viewModel.shoppingMode.collectAsState()
    val currentLocationName by viewModel.currentLocationName.collectAsState()
    val userAddress by viewModel.userAddress.collectAsState()

    // 控制底部弹窗显示/隐藏
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(containerColor = Color.White) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 顶部搜索与定位栏
            item {
                TopSearchBar(
                    shoppingMode = shoppingMode,
                    locationName = currentLocationName,
                    onLocationClick = { showBottomSheet = true }
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 轮播广告区
            item { PromoBanner(banners, navController) }
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // 秒杀 + AI推荐卡片
            item { PromoCardsSection(flashSales, navController) }
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // 每日新品模块标题
            item { SectionTitle("Daily New Arrivals") }
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 新品列表（空状态/商品列表）
            if (newArrivals.isEmpty()) {
                item {
                    Text(
                        text = "Loading fresh arrivals...",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(newArrivals) { product ->
                    ProductItem(product = product, navController = navController)
                }
            }

            // 底部留白（避免被底部导航遮挡）
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // 配送/自提选择底部弹窗
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
                currentMode = shoppingMode,
                currentLocation = currentLocationName,
                userAddress = userAddress,
                onModeSelected = { mode, location ->
                    viewModel.updateDeliveryMode(mode, location)
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

/**
 * 顶部搜索栏：显示配送/自提模式 + 位置 + 搜索/购物车图标
 */
@Composable
private fun TopSearchBar(
    shoppingMode: String,
    locationName: String,
    onLocationClick: () -> Unit
) {
    val prefixText = if (shoppingMode == "pickup") "PICKUP AT" else "DELIVER TO"
    val iconVector = if (shoppingMode == "pickup") Icons.Default.Storefront else Icons.Default.LocationOn

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：配送/自提 + 位置（可点击切换）
        Column(
            modifier = Modifier
                .weight(1F)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onLocationClick() }
                .padding(vertical = 4.dp)
        ) {
            Text(text = prefixText, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(iconVector, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = locationName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DarkText,
                    maxLines = 1
                )
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Drop Down", tint = Color.Gray)
            }
        }

        // 右侧：搜索 + 购物车（带角标）
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = DarkText)
            }
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = "Cart", tint = DarkText)
                }
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp, end = 4.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(BrandOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * 配送/自提选择面板（底部弹窗内容）
 */
@Composable
fun DeliveryModeSelector(
    currentMode: String,
    currentLocation: String,
    userAddress: String,
    onModeSelected: (String, String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 48.dp)
    ) {
        Text("Choose your service", fontSize = 20.sp, fontWeight = FontWeight.Black, color = DarkText)
        Spacer(modifier = Modifier.height(24.dp))

        // 配送到家选项
        ModeOptionCard(
            title = "Delivery to Address",
            subtitle = if (userAddress.isEmpty()) "Tap to add your delivery address" else userAddress,
            icon = Icons.Default.DirectionsBike,
            isSelected = currentMode == "shipping",
            isWarning = userAddress.isEmpty(),
            onClick = {
                if (userAddress.isEmpty()) {
                    onNavigateToProfile()
                } else {
                    onModeSelected("shipping", userAddress)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Or pick up nearby:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        // 自提点1
        ModeOptionCard(
            title = "Market Street Flagship",
            subtitle = "123 Market St, 0.5 miles away",
            icon = Icons.Default.Storefront,
            isSelected = currentMode == "pickup" && currentLocation.contains("Market"),
            isWarning = false,
            onClick = { onModeSelected("pickup", "Market Street Flagship") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 自提点2
        ModeOptionCard(
            title = "GreenLoop Market",
            subtitle = "Downtown, 5th Ave, 1.2 miles away",
            icon = Icons.Default.Storefront,
            isSelected = currentMode == "pickup" && currentLocation.contains("GreenLoop"),
            isWarning = false,
            onClick = { onModeSelected("pickup", "GreenLoop Market") }
        )
    }
}

/**
 * 配送/自提 单个选项卡片（可选中、带警告状态）
 */
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
                color = if (isSelected) BrandGreen else if (isWarning) BrandOrange.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 图标区域
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isSelected) BrandGreen.copy(alpha = 0.1f) else LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) BrandGreen else if (isWarning) BrandOrange else Color.Gray)
        }
        Spacer(modifier = Modifier.width(16.dp))

        // 标题+描述
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = if (isWarning) BrandOrange else Color.Gray, fontSize = 12.sp)
        }

        // 选中/警告状态图标
        if (isSelected) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen)
        } else if (isWarning) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = BrandOrange)
        }
    }
}

/**
 * 轮播Banner（无数据时显示默认占位图）
 */
@Composable
private fun PromoBanner(banners: List<com.lin101.convenience_store.data.model.Banner>, navController: NavHostController) {
    // 无Banner数据时显示默认占位UI
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

    // 轮播状态与自动播放
    val pagerState = rememberPagerState(pageCount = { banners.size })
    LaunchedEffect(banners.size) {
        while (true) {
            delay(3000)
            if (!pagerState.isScrollInProgress) {
                try { pagerState.animateScrollToPage((pagerState.currentPage + 1) % banners.size) } catch (e: Exception) {}
            }
        }
    }

    // 轮播容器 + 指示器
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
        // 底部指示器
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

/**
 * 营销卡片区域：左侧限时秒杀，右侧AI推荐
 */
@Composable
private fun PromoCardsSection(flashSales: List<com.lin101.convenience_store.data.model.Product>, navController: NavHostController) {
    val flashProduct = flashSales.firstOrNull()
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // 左侧：限时秒杀卡片
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
                // 计算折扣
                val original = flashProduct.originalPrice ?: flashProduct.price
                val discountPercent = if (original > 0) ((original - flashProduct.price) / original * 100).roundToInt() else 0

                // 秒杀倒计时
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

                // 秒杀标题 + 倒计时
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

                // 商品图片 + 折扣标签
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
                // 秒杀无数据状态
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Flash Deals", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 右侧：AI推荐卡片
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

/**
 * 倒计时数字胶囊（时分秒）
 */
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

/**
 * 模块标题（左侧标题 + 右侧查看全部）
 */
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

/**
 * 商品列表项（圆形图片 + 名称描述 + 价格 + 加入按钮）
 */
@Composable
private fun ProductItem(product: com.lin101.convenience_store.data.model.Product, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("product_detail/${product.productId}") }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 商品图片
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

        // 商品信息
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(product.description ?: "Fresh arriving", color = Color.Gray, fontSize = 12.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text("$${product.price}", color = BrandGreen, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }

        // 加入按钮
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

/**
 * 计算秒杀活动剩余秒数
 * @param endTimeStr 结束时间字符串
 * @return 剩余秒数（异常返回0）
 */
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