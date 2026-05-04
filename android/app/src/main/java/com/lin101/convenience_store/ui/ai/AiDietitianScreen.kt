package com.lin101.convenience_store.ui.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lin101.convenience_store.data.model.CartItem
// 【核心修改】：統一從你的 Color.kt 導入顏色，消除衝突
import com.lin101.convenience_store.ui.theme.BrandGreen
import com.lin101.convenience_store.ui.theme.BrandOrange
import com.lin101.convenience_store.ui.theme.DarkText
import com.lin101.convenience_store.ui.theme.LightGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiDietitianScreen(
    navController: NavController,
    cartItems: List<CartItem>,
    viewModel: AiDietitianViewModel = viewModel()
) {
    val result by viewModel.analysisResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 进入页面立即触发分析
    LaunchedEffect(Unit) {
        viewModel.analyzeCart(cartItems)
    }

    Scaffold(
        containerColor = LightGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nutri-Radar", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = DarkText)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightGray)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else if (result != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Bento Top: Health Score Card
                ScoreCard(result!!.healthScore)

                // 2. Bento Middle: Nutrition Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // 【解決折行问题】：在卡路里卡片中強制單位同行
                    BentoMiniCard(modifier = Modifier.weight(1f), label = "CALORIES", value = "${result!!.totalCalories}", unit = "kcal", color = BrandOrange, forceOneLine = true)
                    BentoMiniCard(modifier = Modifier.weight(1f), label = "PROTEIN", value = "${result!!.totalProtein}", unit = "g", color = BrandGreen)
                    BentoMiniCard(modifier = Modifier.weight(1f), label = "FAT", value = "${result!!.totalFat}", unit = "g", color = Color.Red)
                }

                // 3. Bento Bottom: 【重新設計】AI Dietitian's Radar Check Card
                InsightCard(result!!)

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun ScoreCard(score: Int) {
    val color = if (score > 70) BrandGreen else BrandOrange
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(12.dp, RoundedCornerShape(32.dp), spotColor = Color.Black.copy(0.05f))
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
            Text("HEALTH SCORE", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Gray, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "$score", fontSize = 80.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = if (score > 70) "PRETTY GOOD!" else "CAREFUL!", fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun BentoMiniCard(modifier: Modifier, label: String, value: String, unit: String, color: Color, forceOneLine: Boolean = false) {
    Box(
        modifier = modifier
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

            // 【核心修改】：解決單位折行问题，確保卡路里和單位在同一行
            if (forceOneLine) {
                // 使用單行文本並強制顯示
                Text(
                    text = "$value $unit",
                    fontSize = 20.sp, // 如果空間有限，略微調小字體
                    fontWeight = FontWeight.Black,
                    color = DarkText,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                )
            } else {
                // 原有的多行顯示邏輯（用於 PROTEIN 和 FAT）
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = DarkText)
                    Text(unit, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp, start = 2.dp), color = Color.Gray)
                }
            }
        }
    }
}

/**
 * 【完全重新設計】：AI Dietitian's Radar Check Card
 * 這個卡片現在採用全白設計（托起內容），包含健康狀況色帶、幽默評論卡和建議列表。
 */
@Composable
fun InsightCard(analysis: com.lin101.convenience_store.data.model.AiModels.AiDietitianResp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White) // 重新設計：使用白色卡片托起內容
            .padding(24.dp)
    ) {
        Column {
            // 标题
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI DIETITIAN'S RADAR CHECK", color = DarkText, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 健康狀況色帶和指针 (可视化指標)
            HealthStatusGauge(score = analysis.healthScore)

            Spacer(modifier = Modifier.height(24.dp))

            // 幽默評論卡片 (类似对话气泡或评论块)
            HumorousCommentCard(comment = analysis.aiComment)

            Spacer(modifier = Modifier.height(24.dp))

            // 详细建议列表
            Text("Practical advice:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // 遍歷後端傳來的建議列表
                analysis.adviceList.forEach { advice ->
                    AdviceItem(advice = advice)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 交互按钮
            Button(
                onClick = { /* TODO: 跳转到调整购物车页面 */ },
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Adjust, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ADJUST MY CART", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

/**
 * 助手組件：健康狀況色帶和指針
 */
@Composable
fun HealthStatusGauge(score: Int) {
    // 根据评分决定颜色等级：健康（绿）、注意（橙）、警告（红）
    val gaugeColor = when {
        score > 70 -> BrandGreen
        score > 50 -> BrandOrange
        else -> Color.Red
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFEFF4)) // 背景色带（灰色）
        ) {
            // 指標塊，從 0 延伸到評分映射位置。
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = score.toFloat() / 100f) // 评分映射到宽度
                    .clip(CircleShape)
                    .background(gaugeColor) // 高亮颜色随评分改变
            )
        }
    }
}

/**
 * 助手組件：幽默評論卡片
 */
@Composable
fun HumorousCommentCard(comment: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E293B)) // 保持幽默点评使用深色背景，突出情緒
            .padding(16.dp)
    ) {
        Text(text = comment, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium, lineHeight = 24.sp)
    }
}

/**
 * 助手組件：單個建議列表項
 */
@Composable
fun AdviceItem(advice: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9FAFB))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = BrandOrange, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = advice, color = Color.DarkGray, fontSize = 13.sp, lineHeight = 18.sp)
    }
}