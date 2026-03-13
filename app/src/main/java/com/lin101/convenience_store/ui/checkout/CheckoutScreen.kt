package com.lin101.convenience_store.ui.checkout

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// 全局颜色常量 - 与项目其他模块保持一致的配色体系
val BrandGreen = Color(0xFF4ADE80) // 品牌主色：绿色
val LightGrayBg = Color(0xFFF7F8FA) // 页面背景浅灰色

/**
 * 结账页面主组件
 * 支持配送(Shipping)和到店自提(Pickup)两种结账模式切换，包含地址/门店选择、支付方式、订单汇总等模块
 * @param navController 导航控制器，用于返回上一页（购物车/商品页）
 */
@Composable
fun CheckoutScreen(navController: NavHostController) {
    // 状态管理：标记当前选中的结账模式（默认选中 配送模式）
    var isShippingSelected by remember { mutableStateOf(true) }

    // 整体布局：垂直列布局，浅灰色背景填充整个屏幕
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
    ) {
        // 1. 顶部导航栏（返回按钮 + 页面标题）
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 返回按钮：点击返回上一页
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            // 页面标题：居中显示
            Text(
                "Checkout",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            // 占位符：与返回按钮宽度一致，保证标题居中
            Spacer(modifier = Modifier.width(40.dp))
        }

        // 2. 可滚动内容区（适配小屏幕，避免内容溢出）
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // 垂直滚动
                .padding(horizontal = 16.dp)
        ) {
            // 2.1 结账模式切换器（Tab 样式）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp)) // 圆角容器
                    .background(Color(0xFFE5E7EB)) // 未选中背景色
                    .padding(4.dp) // 内边距，实现选中项的"悬浮"效果
            ) {
                // 配送模式按钮
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(26.dp))
                        .background(if (isShippingSelected) Color(0xFF0F172A) else Color.Transparent) // 选中时深色背景
                        .clickable { isShippingSelected = true } // 点击切换为配送模式
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Shipping",
                        color = if (isShippingSelected) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 自提模式按钮
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(26.dp))
                        .background(if (!isShippingSelected) Color(0xFF0F172A) else Color.Transparent) // 选中时深色背景
                        .clickable { isShippingSelected = false } // 点击切换为自提模式
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Pickup",
                        color = if (!isShippingSelected) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2.2 动态内容区：根据选中的结账模式渲染不同的卡片
            if (isShippingSelected) {
                ShippingAddressCard() // 配送模式：显示配送地址卡片
            } else {
                PickupStoreCard() // 自提模式：显示自提门店卡片
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2.3 支付方式选择区域（公共模块）
            PaymentMethodSection()
            Spacer(modifier = Modifier.height(24.dp))

            // 2.4 订单汇总 + 下单按钮（公共模块，接收模式状态动态计算金额）
            OrderSummarySection(isShippingSelected)
            Spacer(modifier = Modifier.height(32.dp)) // 底部留白，避免内容被遮挡
        }
    }
}

// --- 以下为拆分的子组件，降低主函数复杂度 ---

/**
 * 配送地址卡片组件
 * 展示当前选中的配送地址，包含地址信息和修改入口
 */
@Composable
private fun ShippingAddressCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)) // 大圆角卡片
            .background(Color.White) // 白色背景
            .padding(20.dp)
    ) {
        // 卡片标题 + 修改按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delivery Address", fontWeight = FontWeight.Bold)
            }
            // 修改地址按钮
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFDCFCE7)) // 浅绿背景
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("CHANGE >", color = BrandGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // 地址详情 + 地图占位
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Home", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("123 Convenience St, Apt 4B,\nMetro City, 10001", color = Color.Gray, fontSize = 14.sp)
            }
            // 地图占位框（后续可替换为实际地图组件）
            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFFD1FAE5)))
        }
    }
}

/**
 * 自提门店卡片组件
 * 展示当前选中的自提门店信息，包含门店名称、地址和修改入口
 */
@Composable
private fun PickupStoreCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        // 卡片标题 + 修改按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Storefront, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pickup Store", fontWeight = FontWeight.Bold)
            }
            // 修改门店按钮
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFDCFCE7))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("CHANGE >", color = BrandGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // 门店详情 + 地图占位
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("GreenLoop Market", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Downtown, 5th Avenue 102", color = Color.Gray, fontSize = 14.sp)
            }
            // 地图占位框
            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFFD1FAE5)))
        }
    }
}

/**
 * 支付方式选择区域组件
 * 展示多种支付方式选项，支持单选切换
 */
@Composable
private fun PaymentMethodSection() {
    Column {
        // 区域标题
        Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        // 支付方式列表容器
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(8.dp)
        ) {
            // 微信支付（默认选中）
            PaymentRow("WeChat Pay", iconVector = Icons.Default.ChatBubble, iconColor = BrandGreen, isSelected = true)
            Divider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(horizontal = 16.dp)) // 分隔线
            // 支付宝（未选中）
            PaymentRow("Alipay", iconVector = Icons.Default.AccountBalanceWallet, iconColor = Color(0xFF3B82F6), isSelected = false)
            Divider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(horizontal = 16.dp))
            // Apple Pay（未选中，使用系统图标临时占位）
            // 备注：后续可替换为本地 SVG 图标，只需传入 iconRes 参数即可
            PaymentRow("Apple Pay", iconVector = Icons.Default.PhoneIphone, iconColor = Color.Black, isSelected = false)
        }
    }
}

/**
 * 通用支付方式行组件
 * 支持系统图标和本地资源图标两种渲染方式
 * @param title 支付方式名称
 * @param iconVector 系统自带图标（二选一）
 * @param iconRes 本地导入的图标资源ID（二选一）
 * @param iconColor 图标背景色
 * @param isSelected 是否为选中状态
 */
@Composable
private fun PaymentRow(
    title: String,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconRes: Int? = null,
    iconColor: Color,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 支付方式图标容器（圆形背景）
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(iconColor),
            contentAlignment = Alignment.Center
        ) {
            // 根据传入的参数类型渲染图标
            if (iconVector != null) {
                Icon(iconVector, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            } else if (iconRes != null) {
                Icon(painterResource(id = iconRes), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        // 支付方式名称（占满剩余宽度）
        Text(title, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        // 选中状态指示器
        if (isSelected) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen) // 选中：绿色对勾
        } else {
            Box(modifier = Modifier.size(24.dp).border(1.dp, Color.LightGray, CircleShape)) // 未选中：灰色边框圆圈
        }
    }
}

/**
 * 订单汇总 + 下单按钮组件
 * 根据结账模式动态计算配送/自提费用和总金额
 * @param isShippingSelected 是否为配送模式
 */
@Composable
private fun OrderSummarySection(isShippingSelected: Boolean) {
    // 动态计算费用：配送模式收2美元配送费，自提模式免费
    val deliveryFee = if (isShippingSelected) "$2.00" else "$0.00"
    val total = if (isShippingSelected) "$26.50" else "$24.50"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        // 小计
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Subtotal", color = Color.Gray)
            Text("$24.50", color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(12.dp))
        // 配送/自提费用（动态显示标题）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(if (isShippingSelected) "Delivery Fee" else "Pickup Fee", color = Color.Gray)
            Text(deliveryFee, color = Color.Gray)
        }
        // 分隔线
        Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF3F4F6))
        // 总计
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(total, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        // 下单按钮
        Button(
            onClick = { /* 后续实现下单逻辑 */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen), // 品牌绿背景
            shape = RoundedCornerShape(16.dp) // 圆角按钮
        ) {
            Text("Place Order", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black)
        }
    }
}