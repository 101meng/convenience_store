package com.lin101.convenience_store.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

// 定义全局使用的十六进制颜色常量
val BrandGreen = Color(0xFF4ADE80)
val DarkGreen = Color(0xFF064E3B)
val LightGray = Color(0xFFF7F8FA)
val LightGreenBg = Color(0xFFE8F5E9)
val OrangeHighlight = Color(0xFFF97316)
val OrangeLightBg = Color(0xFFFFF7ED)

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val banners by viewModel.banners.collectAsState()
    val flashSales by viewModel.flashSales.collectAsState()
    val newArrivals by viewModel.newArrivals.collectAsState() // 收集新品数据

    Scaffold(containerColor = Color.White) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { TopSearchBar() }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 轮播图 (真实数据)
            item { PromoBanner(banners, navController) }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // 中间双列促销模块 (真实秒杀数据)
            item { PromoCardsSection(flashSales, navController) }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // 新品推荐标题
            item { SectionTitle("Daily New Arrivals") }
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 【核心修改】：用 LazyColumn 的 items 渲染真实的列表数据
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

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun TopSearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "DELIVER TO",
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = BrandGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Central Park West, NY", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Drop Down",
                    tint = Color.Gray
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
            }
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = "Cart", tint = Color.Black)
                }
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp, end = 4.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF59E0B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "2",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PromoBanner(
    banners: List<com.lin101.convenience_store.data.model.Banner>,
    navController: NavHostController
) {
    if (banners.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(DarkGreen)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BrandGreen)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "PREMIUM SELECTION",
                        fontSize = 9.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Freshness\nDelivered to\nYour Door",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Up to 40% Off Produce", color = Color(0xCCFFFFFF), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Order Now",
                        color = DarkGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState.currentPage) {
        delay(3000)
        val nextPage = (pagerState.currentPage + 1) % banners.size
        pagerState.animateScrollToPage(nextPage)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val banner = banners[page]
            AsyncImage(
                model = banner.imageUrl,
                contentDescription = "Promo Banner",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { banner.linkUrl?.let { navController.navigate(it) } },
                contentScale = ContentScale.Crop
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(banners.size) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) BrandGreen else Color.White.copy(alpha = 0.5f)
                Box(modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color))
            }
        }
    }
}

@Composable
private fun PromoCardsSection(
    flashSales: List<com.lin101.convenience_store.data.model.Product>,
    navController: NavHostController
) {
    val flashProduct = flashSales.firstOrNull()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Flash Sale Box
        Box(
            modifier = Modifier
                .weight(1f)
                .height(190.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(24.dp))
                .background(Color.White)
                .clip(RoundedCornerShape(24.dp))
                .clickable(enabled = flashProduct != null) {
                    flashProduct?.let { navController.navigate("product_detail/${it.productId}") }
                }
        ) {
            if (flashProduct != null) {
                val original = flashProduct.originalPrice ?: flashProduct.price
                val discountPercent =
                    if (original > 0) ((original - flashProduct.price) / original * 100).roundToInt() else 0

                var remainingSeconds by remember(flashProduct) {
                    mutableStateOf(
                        calculateRemainingSeconds(flashProduct.flashSaleEndTime)
                    )
                }

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
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            tint = OrangeHighlight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Flash Sale", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimePill(if (remainingSeconds > 0) hours else "00")
                        Text(
                            ":",
                            color = OrangeHighlight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TimePill(if (remainingSeconds > 0) minutes else "00")
                        Text(
                            ":",
                            color = OrangeHighlight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                    AsyncImage(
                        model = flashProduct.imageUrl,
                        contentDescription = flashProduct.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "-$discountPercent%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No Flash Deals",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Fresh Bento Box (保持原有静态)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(190.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(LightGreenBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = DarkGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Fresh Bento", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Daily prepped meals", color = Color.Gray, fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.DarkGray)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(BrandGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
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
            .background(OrangeLightBg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = time, color = OrangeHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionTitle(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        Text(text = "See all", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * 【核心重构】：支持真实数据的 ProductItem
 * 样式 100% 保持了你原来的 Box、Spacer 和文字间距。
 */
/**
 * 【核心重构】：支持真实数据的 ProductItem，并将 Add 按钮绑定跳转逻辑
 */
@Composable
private fun ProductItem(product: com.lin101.convenience_store.data.model.Product, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // 点击整行可以跳往该商品的详情页
            .clickable { navController.navigate("product_detail/${product.productId}") }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(LightGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = product.description ?: "Fresh arriving", color = Color.Gray, fontSize = 12.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$${product.price}", color = BrandGreen, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }

        // 【修改点】：给 Add 按钮赋予和点击整行一样的跳转能力
        Button(
            onClick = {
                // 提取商品的真实 ID，拼接成路由路径，命令 Navigation 进行跳转
                navController.navigate("product_detail/${product.productId}")
            },
            colors = ButtonDefaults.buttonColors(containerColor = LightGray, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(36.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Text(text = "Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
    } catch (e: Exception) {
        0L
    }
}