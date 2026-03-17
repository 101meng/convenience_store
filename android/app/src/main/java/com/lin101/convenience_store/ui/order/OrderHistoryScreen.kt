package com.lin101.convenience_store.ui.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.lin101.convenience_store.data.model.OrderModels
import java.util.Locale

val BrandGreen = Color(0xFF4ADE80)
val LightGrayBg = Color(0xFFF7F8FA)

private data class OrderStatusStyle(
    val statusText: String,
    val statusColor: Color,
    val statusBg: Color,
    val btnBgColor: Color,
    val btnTextColor: Color,
    val btnBorder: Color,
    val actionText: String
)

@Composable
fun OrderHistoryScreen(
    navController: NavHostController,
    viewModel: OrderHistoryViewModel = viewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(LightGrayBg)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("My Order", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(vertical = 14.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Ongoing", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(Color.Black))
            }
            Text("Completed", color = Color.Gray, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text("Cancelled", color = Color.Gray, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orders) { order ->
                    OrderCard(order)
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderModels.OrderVO) {
    // 【防崩溃兜底 1】：如果 status 是 null，默认归为 Ongoing
    val safeStatus = order.status ?: "PENDING"

    val style = when (safeStatus.uppercase(Locale.ROOT)) {
        "COMPLETED" -> OrderStatusStyle(
            "Completed",
            BrandGreen,
            Color(0xFFDCFCE7),
            Color.White,
            Color.Black,
            Color.LightGray,
            "Reorder"
        )

        "CANCELLED" -> OrderStatusStyle(
            "Cancelled",
            Color.Red,
            Color(0xFFFEE2E2),
            Color.Transparent,
            Color.Gray,
            Color.Transparent,
            ""
        )

        else -> OrderStatusStyle(
            "Ongoing",
            Color(0xFF3B82F6),
            Color(0xFFDBEAFE),
            Color.Black,
            Color.White,
            Color.Transparent,
            "Track Order"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 【防崩溃兜底 2】：如果 orderSn 是 null，显示默认单号
            Text(order.orderSn ?: "ORD-UNKNOWN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(style.statusBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    style.statusText,
                    color = style.statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 【防崩溃兜底 3（核心元凶）】：如果 createdAt 是 null，提供占位时间防止 Text 崩溃
        Text(order.createdAt ?: "15 Oct 2026, 10:30 AM", color = Color.Gray, fontSize = 13.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            val items = order.items ?: emptyList() // 防止 items 本身为 null

            if (items.isNotEmpty()) {
                Box(modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(LightGrayBg)) {
                    AsyncImage(
                        model = items[0].imageUrl ?: "https://via.placeholder.com/150",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (items.size > 1) {
                Box(modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(LightGrayBg)) {
                    AsyncImage(
                        model = items[1].imageUrl ?: "https://via.placeholder.com/150",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (items.size > 2) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(LightGrayBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "+${items.size - 2}",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), color = LightGrayBg)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "TOTAL PRICE",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    String.format(Locale.US, "$%.2f", order.actualAmount ?: 0.0),
                    color = if (safeStatus == "CANCELLED") Color.Gray else BrandGreen,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }

            if (style.actionText.isNotEmpty()) {
                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = style.btnBgColor,
                        contentColor = style.btnTextColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = if (style.btnBorder != Color.Transparent) BorderStroke(
                        1.dp,
                        style.btnBorder
                    ) else null,
                    modifier = Modifier.height(44.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text(style.actionText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}