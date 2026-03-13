package com.lin101.convenience_store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.ui.navigation.MainScreen
import com.lin101.convenience_store.ui.theme.Convenience_storeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Convenience_storeTheme {
                val context = LocalContext.current
                val userPreferences = remember { UserPreferences(context) }
                val tokenState by userPreferences.tokenFlow.collectAsState(initial = "LOADING")

                if (tokenState == "LOADING") {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF4ADE80))
                    }
                } else {
                    // 判断本地 Token：如果没有，去 login；如果有，直接去 home 首页
                    val startDestination = if (tokenState.isNullOrEmpty()) "login" else "home"

                    // 将计算好的起始页传给统一的骨架屏
                    MainScreen(startDestination = startDestination)
                }
            }
        }
    }
}