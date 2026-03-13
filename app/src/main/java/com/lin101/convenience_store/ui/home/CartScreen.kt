package com.lin101.convenience_store.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

val BrandGreen = Color(0xFF4ADE80)
val LightGrayBg = Color(0xFFF7F8FA)

@Composable
fun CartScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 点击返回按钮回到首页
                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Shopping Cart", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { /* TODO: 清空购物车 */ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.Red)
                }
            }
        },
        bottomBar = {
            CartCheckoutBar(
                onCheckoutClick = {
                    // 执行页面跳转路由
                    navController.navigate("checkout")
                }
            )
        },
        containerColor = LightGrayBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { CartItemCard("Fresh Whole Milk", "1 Liter", "3.50", "2", Color(0xFF93C5FD)) }
            item { CartItemCard("Glazed Choco Donut", "Freshly Baked", "1.20", "4", Color(0xFF374151)) }
            item { CartItemCard("Bolt Energy Drink", "250ml Can", "2.50", "1", Color(0xFF064E3B)) }
            item { CartItemCard("Salted Kettle Chips", "Family Pack 150g", "3.99", "1", Color(0xFFFDE68A)) }
        }
    }
}

@Composable
private fun CartItemCard(title: String, subtitle: String, price: String, qty: String, imageColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)).background(imageColor)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$$price", color = BrandGreen, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(LightGrayBg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = "-", modifier = Modifier.size(16.dp))
                    }
                    Text(qty, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                    Box(
                        modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(BrandGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "+", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CartCheckoutBar(onCheckoutClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)).background(Color.White).padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal", color = Color.Gray)
            Text("$18.29", color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Delivery Fee", color = Color.Gray)
            Text("$1.50", color = Color.Gray)
        }
        Divider(modifier = Modifier.padding(vertical = 16.dp), color = LightGrayBg)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total Amount", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text("$19.79", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCheckoutClick, // 触发外部传入的跳转逻辑
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Proceed to Checkout", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, tint = Color.Black)
        }

        // 底部留出空间，防止被全局导航栏遮挡
        Spacer(modifier = Modifier.height(80.dp))
    }
}