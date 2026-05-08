package com.lin101.convenience_store.ui.product

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 沿用你定义的颜色规范
val BrandGreen = Color(0xFF4ADE80)
val LightGrayBg = Color(0xFFF7F8FA)
val DarkText = Color(0xFF0F172A)

@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productId: Int,
    viewModel: ProductDetailViewModel = viewModel()
) {
    val product by viewModel.product.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 【消除假图标】：增加一个本地收藏状态模拟
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
    }

    // 极致利落的短时间 Snackbar 提示逻辑
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            launch {
                val snackbarJob = launch {
                    snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Indefinite)
                }
                delay(1200)
                snackbarJob.cancel()
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = DarkText,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = BrandGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(data.visuals.message, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        },
        bottomBar = {
            // 底部购买栏保持不变，逻辑已接入 ViewModel
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(LightGrayBg).padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Remove, "Minus", Modifier.size(24.dp).clickable { viewModel.decreaseQuantity() })
                        Text(quantity.toString(), Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Icon(Icons.Default.Add, "Plus", Modifier.size(24.dp).clickable { viewModel.increaseQuantity() })
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    Button(
                        onClick = { viewModel.addToCart() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkText),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, null, Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Cart", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (product == null) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
            return@Scaffold
        }

        val p = product!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // 顶部图片区域
            Box(Modifier.fillMaxWidth().height(320.dp)) {
                AsyncImage(
                    model = p.imageUrl,
                    contentDescription = p.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = DarkText)
                    }

                    // 【修复假按钮】：现在点击会切换颜色
                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier.background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else DarkText
                        )
                    }
                }
            }

            // 商品信息区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-24).dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                // 1. 动态展示标签
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    p.tag1?.let { ProductTag(it) }
                    p.tag2?.let { ProductTag(it) }
                    p.tag3?.let { ProductTag(it) }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(p.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = DarkText)

                Spacer(modifier = Modifier.height(8.dp))
                Text("$${p.price}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = BrandGreen)

                Spacer(modifier = Modifier.height(24.dp))

                // 2. 【核心修复】：绑定真实的营养成分数据
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    NutritionItem("Calories", "${p.calories ?: 0} kcal")
                    NutritionItem("Protein", "${p.protein ?: 0}g")
                    NutritionItem("Fat", "${p.totalFat ?: 0}g")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. 【核心修复】：绑定真实的描述文本
                Text("Description", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = p.description ?: "No description available for this fresh item.",
                    color = Color.Gray,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

/**
 * 助手组件：单项营养指标卡片
 */
@Composable
fun NutritionItem(title: String, value: String) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF7F8FA))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = DarkText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

/**
 * 助手组件：商品标签
 */
@Composable
fun ProductTag(text: String) {
    if (text.isBlank()) return
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFDCFCE7))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = Color(0xFF166534), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}