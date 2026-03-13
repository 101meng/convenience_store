package com.lin101.convenience_store.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val BrandGreen = Color(0xFF4ADE80)
val LightGrayBg = Color(0xFFF7F8FA)

@Composable
fun OrderHistoryScreen() {
    Column(modifier = Modifier.fillMaxSize().background(LightGrayBg)) {
        // 顶部导航
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            Text("Order History", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
        }

        // 自定义 Tab 栏
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(30.dp)).background(Color.White).padding(4.dp)
        ) {
            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(26.dp)).border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(26.dp)).background(Color.White).padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                Text("All Orders", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Box(modifier = Modifier.weight(1f).padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                Text("Ongoing", color = Color.Gray, fontSize = 13.sp)
            }
            Box(modifier = Modifier.weight(1f).padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                Text("Past", color = Color.Gray, fontSize = 13.sp)
            }
        }

        // 订单列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { OrderCard("#ORD-2023-084", "COMPLETED", "Oct 24, 2023 • 14:30", "32.40", "Reorder") }
            item { OrderCard("#ORD-2023-091", "DELIVERING", "Today • 11:15", "12.50", "Track") }
            item { OrderCard("#ORD-2023-079", "CANCELLED", "Oct 20, 2023 • 09:20", "4.90", "View Details") }
        }
    }
}

@Composable
private fun OrderCard(orderId: String, status: String, date: String, price: String, actionText: String) {
    // 状态颜色映射逻辑
    val statusBgColor = when (status) {
        "COMPLETED" -> Color(0xFFE8F5E9)
        "DELIVERING" -> Color(0xFFDBEAFE)
        else -> Color(0xFFFEE2E2)
    }
    val statusTextColor = when (status) {
        "COMPLETED" -> BrandGreen
        "DELIVERING" -> Color(0xFF3B82F6)
        else -> Color(0xFFEF4444)
    }

    // 按钮样式映射
    val isPrimaryAction = actionText == "Reorder"
    val btnBgColor = if (isPrimaryAction) BrandGreen else Color.Transparent
    val btnTextColor = if (isPrimaryAction) Color.White else if (actionText == "Track") BrandGreen else Color.Gray
    val btnBorder = if (actionText == "Track") BrandGreen else if (!isPrimaryAction) Color(0xFFE5E7EB) else Color.Transparent

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White).padding(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(orderId, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(statusBgColor).padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text(status, color = statusTextColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(date, color = Color.Gray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // 此处原图设计为圆形商品缩略图
        Row {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFFBBF24)))
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFD1D5DB)))
            Spacer(modifier = Modifier.width(8.dp))
            if (status == "COMPLETED") {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF9CA3AF)))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(LightGrayBg), contentAlignment = Alignment.Center) {
                    Text("+2", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp), color = LightGrayBg)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("TOTAL PRICE", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("$$price", color = if (status == "CANCELLED") Color.Gray else BrandGreen, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = btnBgColor, contentColor = btnTextColor),
                shape = RoundedCornerShape(16.dp),
                border = if (btnBorder != Color.Transparent) androidx.compose.foundation.BorderStroke(1.dp, btnBorder) else null,
                modifier = Modifier.height(40.dp)
            ) {
                Text(actionText, fontWeight = FontWeight.Bold)
            }
        }
    }
}