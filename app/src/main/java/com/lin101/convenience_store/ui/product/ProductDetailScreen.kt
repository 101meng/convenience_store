package com.lin101.convenience_store.ui.product

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

val BrandGreen = Color(0xFF4ADE80)
val DarkText = Color(0xFF0F172A)
val TextGray = Color(0xFF9CA3AF)
val LightGrayBg = Color(0xFFF8FAFC)

@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productId: Int = 1, // 暂时默认传入 1，后面从路由中动态获取
    viewModel: ProductDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val quantity by viewModel.quantity.collectAsState()
    val product by viewModel.product.collectAsState()

    // 监听 Toast 提示
    LaunchedEffect(Unit) {
        viewModel.loadProductDetail(productId)
        viewModel.uiEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // 使用 Box 作为根布局，方便实现底部悬浮栏和顶部沉浸式按钮
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // 可滚动的主内容区
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // 给底部悬浮栏留出空间
        ) {
            // 1. 顶部商品主图 (带圆角过渡)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(LightGrayBg)
            ) {
                product?.imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Product Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // 2. 商品信息区
            Column(modifier = Modifier.padding(24.dp)) {
                // 标签区
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    product?.tag1?.let { ProductTag(it) }
                    product?.tag2?.let { ProductTag(it) }
                    product?.tag3?.let { ProductTag(it) }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 标题与价格
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product?.name ?: "Loading...",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkText,
                        modifier = Modifier.weight(1f),
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "$${product?.price ?: "0.00"}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandGreen
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 商品描述区
                Text(text = "Description", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "A delicious and healthy bowl featuring fresh avocado, organic quinoa, cherry tomatoes, and a zesty lemon dressing. Perfect for a quick, nutritious lunch or light dinner.",
                    fontSize = 14.sp,
                    color = TextGray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 营养成分 (占位UI，增加页面的高级感)
                Text(text = "Nutritional Info", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    NutritionItem("Calories", "320 kcal")
                    NutritionItem("Proteins", "12g")
                    NutritionItem("Fats", "18g")
                    NutritionItem("Carbs", "45g")
                }
            }
        }

        // 3. 顶部绝对定位的导航按钮 (悬浮在图片上方)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 返回按钮
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.8f)).clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkText)
            }
            // 收藏按钮
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.8f)).clickable { /* TODO: Toggle Favorite */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = DarkText)
            }
        }

        // 4. 底部固定的操作栏 (选择数量 + 加入购物车)
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().shadow(16.dp),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 数量选择器
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(100.dp)).background(LightGrayBg).padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.decreaseQuantity() },
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = quantity.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    IconButton(
                        onClick = { viewModel.increaseQuantity() },
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(BrandGreen)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Plus", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // 加入购物车按钮
                Button(
                    onClick = { viewModel.addToCart() },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkText),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add to Cart", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 提取的商品标签组件
@Composable
fun ProductTag(text: String) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFDCFCE7)).padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = Color(0xFF166534), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// 提取的营养成分小卡片组件
@Composable
fun NutritionItem(title: String, value: String) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(LightGrayBg).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontSize = 10.sp, color = TextGray)
    }
}