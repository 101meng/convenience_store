package com.lin101.convenience_store.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage // 确保引入了 Coil
import java.util.Locale

val LightGrayBg = Color(0xFFF8FAFC)
val DarkText = Color(0xFF0F172A)
val BrandGreen = Color(0xFF4ADE80)
val DarkBlue = Color(0xFF0F172A)
val DangerRed = Color(0xFFEF4444)

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    // 监听 ViewModel 中传来的真实用户数据
    val nickname by viewModel.nickname.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val avatarUrl by viewModel.avatarUrl.collectAsState()
    // 【新增】监听真实的余额数据
    val balance by viewModel.balance.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部操作栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White).clickable { /* TODO: Settings */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Gray)
            }
            Text("My Account", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications", tint = Color.Gray)
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(6.dp).clip(CircleShape).background(Color(0xFFF97316)))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 头像与信息区域 (已接入真实数据)
        Box(modifier = Modifier.size(100.dp)) {
            // 动态判断是否有头像
            if (avatarUrl.isNotEmpty()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 没有头像时显示默认占位底色
                Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(32.dp)).background(Color(0xFFE8F5E9)))
            }

            // 编辑小图标
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 8.dp, y = 8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { navController.navigate("edit_profile") },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // 使用监听到的真实数据替换假名
        Text(nickname, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = DarkText)
        Spacer(modifier = Modifier.height(4.dp))
        // 使用监听到的真实数据替换假手机号
        Text(phone, fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        // 编辑资料入口卡片
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .clickable { navController.navigate("edit_profile") }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFEEF2FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF6366F1))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("EDIT PROFILE", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 余额与订单双列卡片
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // 余额卡片
            Column(modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)).background(Color.White).padding(20.dp)) {
                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFDCFCE7)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = BrandGreen)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("BALANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))

                // ✅ 【修改】：使用真实的余额数据并格式化，解决 Locale 警告
                Text(String.format(Locale.US, "$%.2f", balance), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Top Up", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // 最近订单卡片
            Column(modifier = Modifier.weight(1f).height(190.dp).clip(RoundedCornerShape(24.dp)).background(Color.White).clickable {
                navController.navigate("orderHistory")
            }.padding(20.dp)) {
                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFEFF6FF)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color(0xFF3B82F6))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("LAST ORDER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Pending\nDelivery", fontSize = 14.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
                Spacer(modifier = Modifier.weight(1f))

                // 重叠商品图标展示
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF064E3B)))
                    Box(modifier = Modifier.size(32.dp).offset(x = (-8).dp).clip(CircleShape).background(Color(0xFF64748B)))
                    Box(
                        modifier = Modifier.size(32.dp).offset(x = (-16).dp).clip(CircleShape).background(LightGrayBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 设置与安全区
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("SETTINGS & SECURITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(12.dp))

        SettingRowItem(Icons.Default.Lock, "Privacy Settings")
        Spacer(modifier = Modifier.height(12.dp))
        SettingRowItem(Icons.Default.HelpOutline, "Customer Support")
        Spacer(modifier = Modifier.height(12.dp))

        // 退出登录按钮
        Row(
            modifier = Modifier
                // ...
                .clickable {
                    viewModel.logout(
                        onSuccess = {
                        }
                    )
                }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = DangerRed)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DangerRed)
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun SettingRowItem(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White).clickable {  }.padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(LightGrayBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.Gray)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}