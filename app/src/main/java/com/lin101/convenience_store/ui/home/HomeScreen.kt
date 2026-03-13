package com.lin101.convenience_store.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 定义全局使用的十六进制颜色常量
val BrandGreen = Color(0xFF4ADE80)
val DarkGreen = Color(0xFF064E3B)
val LightGray = Color(0xFFF7F8FA)
val LightGreenBg = Color(0xFFE8F5E9)
val OrangeHighlight = Color(0xFFF97316)
val OrangeLightBg = Color(0xFFFFF7ED)

/**
 * 首页主容器组件。
 * 使用 Scaffold 提供基础页面结构，LazyColumn 提供垂直滚动能力。
 */
@Composable
fun HomeScreen() {
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { TopSearchBar() }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { PromoBanner() }
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // 新增的中间双列促销模块
            item { PromoCardsSection() }
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item { SectionTitle("Daily New Arrivals") }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { ProductItem("Artisanal Cold Brew", "350ml • Low acidity", "4.50") }
            item { ProductItem("Classic Butter Croissant", "Baked fresh today", "2.25") }
            item { ProductItem("Mixed Berry Smoothie Bowl", "Organic • 250g", "6.95") }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

/**
 * 顶部导航栏组件。
 * 包含定位信息及搜索、购物车功能入口。
 */
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
            Text(text = "DELIVER TO", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = BrandGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Central Park West, NY", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Drop Down", tint = Color.Gray)
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
                    Text(text = "2", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * 顶部主干促销横幅。
 */
@Composable
private fun PromoBanner() {
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
                Text(text = "PREMIUM SELECTION", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Freshness\nDelivered to\nYour Door", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Up to 40% Off Produce", color = Color(0xCCFFFFFF), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(text = "Order Now", color = DarkGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * 中部双列促销模块。
 * 包含 Flash Sale 和 Fresh Bento 两个卡片。使用 weight(1f) 实现等宽排列。
 */
@Composable
private fun PromoCardsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp) // 控制两个卡片之间的间距
    ) {
        // 左侧卡片：Flash Sale
        Box(
            modifier = Modifier
                .weight(1f) // 占据可用空间的 50%
                .height(190.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(24.dp))
                .background(Color.White)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = OrangeHighlight, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Flash Sale", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))

                // 倒计时 UI
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    TimePill("02")
                    Text(":", color = OrangeHighlight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    TimePill("45")
                    Text(":", color = OrangeHighlight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    TimePill("12")
                }
            }

            // 底部图片占位与角标
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkGreen) // 模拟商品图片背景
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("-50%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 右侧卡片：Fresh Bento
        Box(
            modifier = Modifier
                .weight(1f)
                .height(190.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(LightGreenBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Fastfood, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Fresh Bento", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Daily prepped meals", color = Color.Gray, fontSize = 11.sp)
            }

            // 底部图片占位与按钮
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.DarkGray) // 模拟商品图片背景
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
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

/**
 * 倒计时数字药丸组件。用于 Flash Sale 卡片。
 */
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

/**
 * 分类标题组件。
 */
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
 * 列表商品项组件。
 */
@Composable
private fun ProductItem(name: String, desc: String, price: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(LightGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$$price", color = BrandGreen, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = LightGray, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(36.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Text(text = "Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}