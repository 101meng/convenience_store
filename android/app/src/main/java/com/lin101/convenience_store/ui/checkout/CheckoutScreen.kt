package com.lin101.convenience_store.ui.checkout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

val BrandGreen = Color(0xFF4ADE80)
val LightGrayBg = Color(0xFFF7F8FA)

@Composable
fun CheckoutScreen(
    navController: NavHostController,
    viewModel: CheckoutViewModel = viewModel()
) {
    val context = LocalContext.current

    // 监听 ViewModel 中的状态
    val isPickup by viewModel.isPickup.collectAsState()
    val selectedStoreId by viewModel.selectedStoreId.collectAsState()
    val selectedPayment by viewModel.selectedPayment.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()

    // 监听成功或失败的 Toast 提示
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // 动态计算费用
    val deliveryFee = if (isPickup) 0.0 else 1.50
    val totalAmount = subtotal + deliveryFee

    Column(modifier = Modifier.fillMaxSize().background(LightGrayBg)) {
        // 1. 顶部导航
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Checkout", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(40.dp))
        }

        // 2. 主体可滚动区域
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {

            // 2.1 配送/自提切换器
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp)).background(Color(0xFFE5E7EB)).padding(4.dp)) {
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(26.dp))
                        .background(if (!isPickup) Color(0xFF0F172A) else Color.Transparent)
                        .clickable { viewModel.setPickup(false) }.padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) { Text("Shipping", color = if (!isPickup) Color.White else Color.Gray, fontWeight = FontWeight.Bold) }

                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(26.dp))
                        .background(if (isPickup) Color(0xFF0F172A) else Color.Transparent)
                        .clickable { viewModel.setPickup(true) }.padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) { Text("Pickup", color = if (isPickup) Color.White else Color.Gray, fontWeight = FontWeight.Bold) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2.2 动态显示地址或门店
            if (!isPickup) {
                ShippingAddressCard()
            } else {
                PickupStoreCard(selectedStoreId) { newId -> viewModel.setStore(newId) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2.3 完美还原的支付方式选择模块 (带动态状态交互)
            PaymentMethodSection(
                selectedPayment = selectedPayment,
                onPaymentSelect = { payment -> viewModel.setPayment(payment) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2.4 订单总览
            OrderSummarySection(isPickup, subtotal, deliveryFee, totalAmount)

            Spacer(modifier = Modifier.height(32.dp))
        }

        // 3. 底部悬浮下单栏
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), color = Color.White, shadowElevation = 24.dp) {
            Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Payment", color = Color.Gray, fontSize = 14.sp)
                    Text(String.format("$%.2f", totalAmount), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = BrandGreen)
                }
                Button(
                    // 【核心触发】：点击进行真实的下单请求！
                    onClick = {
                        viewModel.submitOrder(
                            onSuccess = {
                                // 下单成功后，清空路由栈，直接跳回首页！
                                navController.navigate("home") {
                                    popUpTo(0) // 清空所有历史页面
                                }
                            }
                        )
                    },
                    modifier = Modifier.height(56.dp).width(160.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Place Order", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ShippingAddressCard() {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White).padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delivery Address", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Central Park West, NY 10025", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun PickupStoreCard(selectedStoreId: Int, onSelectStore: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Storefront, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pickup Store", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 动态门店选项列表
        StoreItemRow(id = 1, name = "Market Street Flagship", address = "123 Market St", isSelected = selectedStoreId == 1) { onSelectStore(1) }
        Spacer(modifier = Modifier.height(8.dp))
        StoreItemRow(id = 2, name = "GreenLoop Market", address = "Downtown, 5th Ave", isSelected = selectedStoreId == 2) { onSelectStore(2) }
    }
}

@Composable
private fun StoreItemRow(id: Int, name: String, address: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .border(2.dp, if (isSelected) BrandGreen else Color.Transparent, RoundedCornerShape(16.dp))
            .background(LightGrayBg).clickable { onClick() }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
            Text(address, color = Color.Gray, fontSize = 12.sp)
        }
        if (isSelected) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen)
    }
}

/**
 * 【你设计的精美支付选择模块】完美还原版
 */
@Composable
private fun PaymentMethodSection(selectedPayment: String, onPaymentSelect: (String) -> Unit) {
    Column {
        Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White).padding(8.dp)) {

            PaymentRow(
                title = "WeChat Pay", iconVector = Icons.Default.ChatBubble, iconColor = BrandGreen,
                isSelected = selectedPayment == "WeChat Pay", onClick = { onPaymentSelect("WeChat Pay") }
            )
            HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(horizontal = 16.dp))

            PaymentRow(
                title = "Alipay", iconVector = Icons.Default.AccountBalanceWallet, iconColor = Color(0xFF3B82F6),
                isSelected = selectedPayment == "Alipay", onClick = { onPaymentSelect("Alipay") }
            )
            HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(horizontal = 16.dp))

            PaymentRow(
                title = "Apple Pay", iconVector = Icons.Default.PhoneIphone, iconColor = Color.Black,
                isSelected = selectedPayment == "Apple Pay", onClick = { onPaymentSelect("Apple Pay") }
            )
        }
    }
}

@Composable
private fun PaymentRow(title: String, iconVector: ImageVector, iconColor: Color, isSelected: Boolean, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(iconColor), contentAlignment = Alignment.Center) {
            Icon(iconVector, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))

        if (isSelected) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen)
        } else {
            Box(modifier = Modifier.size(24.dp).border(1.dp, Color.LightGray, CircleShape))
        }
    }
}

@Composable
private fun OrderSummarySection(isPickup: Boolean, subtotal: Double, deliveryFee: Double, total: Double) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White).padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal", color = Color.Gray)
            Text(String.format("$%.2f", subtotal), color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(if (isPickup) "Pickup Fee" else "Delivery Fee", color = Color.Gray)
            Text(String.format("$%.2f", deliveryFee), color = Color.Gray)
        }
    }
}