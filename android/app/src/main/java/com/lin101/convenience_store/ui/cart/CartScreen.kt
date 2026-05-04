package com.lin101.convenience_store.ui.cart

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.lin101.convenience_store.data.model.CartItem
import java.util.Locale

val BrandGreen = Color(0xFF4ADE80)
val BgOffWhite = Color(0xFFF7F8FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, viewModel: CartViewModel = viewModel()) {
    val cartItems by viewModel.cartItems.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.fetchCartList()
    }
    Scaffold(
        containerColor = BgOffWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Cart", fontWeight = FontWeight.Black, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgOffWhite)
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .padding(24.dp)
                ) {
                    // ==========================================
                    // 【新增】：AI 营养分析雷达入口按钮
                    // ==========================================
                    Button(
                        onClick = {
                            // 1. 将购物车列表转换为 JSON 字符串
                            val cartJson = Gson().toJson(cartItems)
                            // 2. 对 JSON 进行 URL 编码，防止特殊字符阻断路由
                            val encodedJson = Uri.encode(cartJson)
                            // 3. 携带参数跳转到 AI 页面
                            navController.navigate("ai_dietitian/$encodedJson")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF4F0FF),
                            contentColor = Color(0xFF9333EA)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Nutritional Analysis", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = String.format(Locale.US, "$%.2f", totalPrice),
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp
                            )
                        }
                        Button(
                            onClick = { navController.navigate("checkout") },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                            shape = CircleShape,
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text("Checkout", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(100.dp).background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🛒", fontSize = 40.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your cart is empty", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { viewModel.updateQuantity(item.cartId, item.quantity + 1) },
                        onDecrease = { viewModel.updateQuantity(item.cartId, item.quantity - 1) },
                        onRemove = { viewModel.removeItem(item.cartId) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, onIncrease: () -> Unit, onDecrease: () -> Unit, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(BgOffWhite)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(String.format(Locale.US, "$%.2f", item.price), color = BrandGreen, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove",
                tint = Color.Red.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp).clickable { onRemove() }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(BgOffWhite).padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(16.dp).clickable { onDecrease() })
                Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Icon(Icons.Default.Add, contentDescription = "Plus", modifier = Modifier.size(16.dp).clickable { onIncrease() })
            }
        }
    }
}