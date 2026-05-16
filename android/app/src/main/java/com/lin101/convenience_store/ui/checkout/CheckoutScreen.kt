package com.lin101.convenience_store.ui.checkout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.lin101.convenience_store.R
import java.util.Locale

val BrandGreen = Color(0xFF4ADE80)
val LightGrayBg = Color(0xFFF7F8FA)
val DarkText = Color(0xFF0F172A)

// ---------- 支付方式数据模型（使用本地 drawable 资源） ----------
data class PaymentMethod(
    val code: String,
    val title: String,
    val iconRes: Int,           // 本地 drawable 资源 ID
    val iconBgColor: Color
)

val paymentMethods = listOf(
    PaymentMethod("wechat", "WeChat Pay", R.drawable.ic_wechat_pay, Color(0xFF2BAD00)),
    PaymentMethod("alipay", "Alipay", R.drawable.ic_alipay, Color(0xFF1677FF)),
    PaymentMethod("apple_pay", "Apple Pay", R.drawable.ic_apple_pay, Color.Black)
)

// ---------- 主页面 ----------
@Composable
fun CheckoutScreen(
    navController: NavHostController,
    viewModel: CheckoutViewModel = viewModel()
) {
    val context = LocalContext.current

    val shoppingMode by viewModel.shoppingMode.collectAsState()
    val storeName by viewModel.storeName.collectAsState()
    val deliveryAddress by viewModel.deliveryAddress.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val selectedPayment by viewModel.selectedPayment.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val deliveryFee = if (shoppingMode == "pickup") 0.0 else 1.50
    val totalAmount = subtotal + deliveryFee

    Scaffold(
        containerColor = LightGrayBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    "Checkout",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(40.dp))
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                shadowElevation = 24.dp
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Payment", color = Color.Gray, fontSize = 14.sp)
                        Text(
                            String.format(Locale.US, "$%.2f", totalAmount),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandGreen
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.submitOrder(
                                onSuccess = {
                                    navController.navigate("home") {
                                        popUpTo(0)
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .width(160.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkText),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Place Order",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // 配送地址或自提门店信息
            item {
                if (shoppingMode == "shipping") {
                    ShippingAddressCard(address = deliveryAddress.ifEmpty { "Please set your address in Profile" })
                } else {
                    PickupStoreCard(storeName = storeName)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 商品列表标题
            item {
                Text(
                    "Order Items",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // 购物车商品明细
            items(cartItems) { item ->
                CartItemRow(item = item)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 支付方式选择
            item {
                Spacer(modifier = Modifier.height(8.dp))
                PaymentMethodSection(
                    selectedCode = selectedPayment,
                    onPaymentSelect = { code -> viewModel.selectPayment(code) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 订单金额总结
            item {
                OrderSummarySection(
                    isPickup = shoppingMode == "pickup",
                    subtotal = subtotal,
                    deliveryFee = deliveryFee
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ---------- 子组件 ----------
@Composable
private fun ShippingAddressCard(address: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = BrandGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delivery Address", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(address, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun PickupStoreCard(storeName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Storefront,
                contentDescription = null,
                tint = BrandGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pickup Store", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(storeName.ifEmpty { "Select Store" }, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun CartItemRow(item: com.lin101.convenience_store.data.model.CartItem) {
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
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(LightGrayBg)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Qty: ${item.quantity} × $${String.format("%.2f", item.price)}",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
        Text(
            String.format(Locale.US, "$%.2f", item.price * item.quantity),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = BrandGreen
        )
    }
}

@Composable
private fun PaymentMethodSection(
    selectedCode: String,
    onPaymentSelect: (String) -> Unit
) {
    Column {
        Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

        paymentMethods.forEach { method ->
            val isSelected = selectedCode == method.code
            PaymentMethodCard(
                method = method,
                isSelected = isSelected,
                onClick = { onPaymentSelect(method.code) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PaymentMethodCard(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = 2.dp,
                color = if (isSelected) BrandGreen else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 使用本地 drawable 真实 Logo
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(method.iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = method.iconRes),
                contentDescription = method.title,
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(method.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
            val description = when (method.code) {
                "wechat" -> "Fast payment via WeChat"
                "alipay" -> "Secure online payment"
                "apple_pay" -> "Pay with Face ID"
                else -> ""
            }
            if (description.isNotBlank()) {
                Text(description, fontSize = 12.sp, color = Color.Gray)
            }
        }
        if (isSelected) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen)
        }
    }
}

@Composable
private fun OrderSummarySection(isPickup: Boolean, subtotal: Double, deliveryFee: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal", color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(
                String.format(Locale.US, "$%.2f", subtotal),
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                if (isPickup) "Pickup Fee" else "Delivery Fee",
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                String.format(Locale.US, "$%.2f", deliveryFee),
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total", fontWeight = FontWeight.Bold)
            Text(
                String.format(Locale.US, "$%.2f", subtotal + deliveryFee),
                fontWeight = FontWeight.Bold,
                color = BrandGreen
            )
        }
    }
}