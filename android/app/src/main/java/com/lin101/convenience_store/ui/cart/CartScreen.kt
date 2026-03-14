package com.lin101.convenience_store.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.lin101.convenience_store.data.model.CartItem

val BrandGreen = Color(0xFF4ADE80)
val LightGrayBg = Color(0xFFF7F8FA)

@Composable
fun CartScreen(
    navController: NavHostController,
    viewModel: CartViewModel = viewModel() // 注入我们刚才写的 ViewModel
) {
    // 监听 ViewModel 中的状态
    val cartItems by viewModel.cartItems.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()

    // 每次进入页面都刷新一下最新的购物车数据
    LaunchedEffect(Unit) {
        viewModel.fetchCartList()
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 返回按钮
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(LightGrayBg).clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("My Cart", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(40.dp)) // 占位保持标题居中
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // 如果购物车为空，显示提示
            if (cartItems.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Your cart is empty", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                // 动态渲染真实的购物车商品列表
                LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                    items(cartItems, key = { it.cartId }) { item ->
                        CartItemCard(
                            item = item,
                            onIncrease = { viewModel.updateQuantity(item.cartId, item.quantity + 1) },
                            onDecrease = { viewModel.updateQuantity(item.cartId, item.quantity - 1) },
                            onRemove = { viewModel.removeItem(item.cartId) }
                        )
                    }
                }
            }

            // 底部结算区域
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // 动态展示商品总价
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", color = Color.Gray)
                        Text(String.format("$%.2f", totalPrice), color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // 假设运费固定为 $1.50
                    val deliveryFee = if (cartItems.isEmpty()) 0.0 else 1.50
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery Fee", color = Color.Gray)
                        Text(String.format("$%.2f", deliveryFee), color = Color.Gray)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = LightGrayBg)

                    // 最终支付总额
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Amount", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(String.format("$%.2f", totalPrice + deliveryFee), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // 结算按钮
                    Button(
                        onClick = { navController.navigate("checkout") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(16.dp),
                        enabled = cartItems.isNotEmpty() // 没东西不让点
                    ) {
                        Text("Proceed to Checkout", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, tint = Color.Black)
                    }
                }
            }
        }
    }
}

/**
 * 提取出来的真实购物车商品卡片组件
 */
@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(LightGrayBg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Coil 动态加载真实的商品图片
        Box(
            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(Color.White)
        ) {
            AsyncImage(
                model = item.imageUrl ?: "https://via.placeholder.com/150",
                contentDescription = item.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 商品名和价格
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(String.format("$%.2f", item.price), color = BrandGreen, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 数量加减与删除控制器
        Column(horizontalAlignment = Alignment.End) {
            // 删除按钮
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove",
                tint = Color.Red.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp).clickable { onRemove() }
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 加减框
            Row(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.White).padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = "Minus",
                    modifier = Modifier.size(16.dp).clickable { onDecrease() }
                )
                Text(
                    text = item.quantity.toString(), // 真实数量
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Plus",
                    modifier = Modifier.size(16.dp).clickable { onIncrease() }
                )
            }
        }
    }
}