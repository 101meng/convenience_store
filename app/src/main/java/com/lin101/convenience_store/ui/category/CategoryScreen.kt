package com.lin101.convenience_store.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.lin101.convenience_store.data.model.Category
import com.lin101.convenience_store.data.model.Product

val BrandGreen = Color(0xFF4ADE80)
val LightGray = Color(0xFFF7F8FA)
val TextGray = Color(0xFF9CA3AF)

@Composable
fun CategoryScreen(
    navController: NavHostController,
    viewModel: CategoryViewModel = viewModel() // 注入 ViewModel
) {
    // 收集 ViewModel 暴露的状态数据，状态改变时 UI 自动重组
    val categories by viewModel.categories.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()

    // 查找当前选中的分类名称，用于显示标题
    val currentCategoryName = categories.find { it.categoryId == selectedCategoryId }?.categoryName?.uppercase() ?: ""

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Text(
            text = "Categories",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search snacks, drinks...", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = LightGray,
                    focusedContainerColor = LightGray,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = BrandGreen)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            // 左侧：分类导航栏 (动态渲染)
            LazyColumn(
                modifier = Modifier.width(80.dp).fillMaxHeight().background(Color.White)
            ) {
                items(categories) { category ->
                    CategorySidebarItem(
                        category = category,
                        isSelected = category.categoryId == selectedCategoryId,
                        onClick = { viewModel.selectCategory(category.categoryId) }
                    )
                }
            }

            // 右侧：商品内容区
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "$currentCategoryName FOOD",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 动态网格布局，列数为 2
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f) // 占满剩余空间
                ) {
                    items(products) { product ->
                        CategoryGridCard(product = product) {
                            // TODO: 后续可以将 productId 传给详情页
                            navController.navigate("product_detail")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 侧边栏子项组件
 */
@Composable
private fun CategorySidebarItem(category: Category, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) BrandGreen else Color.Transparent
    val contentColor = if (isSelected) Color.White else TextGray
    val textColor = if (isSelected) BrandGreen else TextGray

    // 图标映射逻辑：根据数据库分类名称匹配对应的 Material 图标
    val icon = when (category.categoryName) {
        "Fresh" -> Icons.Default.Restaurant
        "Snacks" -> Icons.Default.Cookie
        "Drinks" -> Icons.Default.LocalDrink
        "Bakery" -> Icons.Default.BakeryDining
        else -> Icons.Default.Category
    }

    Column(
        modifier = Modifier.fillMaxWidth().height(80.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(16.dp)).background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = category.categoryName, tint = contentColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(category.categoryName.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

/**
 * 商品网格卡片组件
 */
@Composable
private fun CategoryGridCard(product: Product, onClick: () -> Unit) {
    Box(
        modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(Color(0xFFE5E7EB)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
            // 此处暂时使用统一占位图标，后续接入 Coil 加载 product.imageUrl
            Icon(Icons.Default.Fastfood, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(text = "$${product.price}", color = BrandGreen, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
        }
    }
}