package com.lin101.convenience_store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lin101.convenience_store.ui.navigation.MainScreen
import com.lin101.convenience_store.ui.theme.Convenience_storeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Convenience_storeTheme {
                MainScreen()
            }
        }
    }
}