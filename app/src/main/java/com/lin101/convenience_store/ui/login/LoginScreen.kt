package com.lin101.convenience_store.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

val ButtonActiveGreen = Color(0xFF40D375)
val ButtonInactiveGreen = Color(0xFF87DBA3)
val DarkGreenLogo = Color(0xFF166534)
val FieldBg = Color(0xFFF8FAFC)
val TextDark = Color(0xFF0F172A)
val TextGray = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = viewModel() // 统一名称为 LoginViewModel
) {
    val phone by viewModel.phone.collectAsState()
    val code by viewModel.code.collectAsState()
    val isCountingDown by viewModel.isCountingDown.collectAsState()
    val countdownSeconds by viewModel.countdownSeconds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val isPhoneValid = phone.length == 11
    val isCodeValid = code.length == 6
    val isLoginEnabled = isPhoneValid && isCodeValid && !isLoading

    val context = LocalContext.current

    // 监听 ViewModel 抛出的单次事件，并用 Toast 弹窗显示
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 顶部导航 (关闭按钮)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(40.dp).clip(CircleShape).background(FieldBg)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDark)
            }
            Text(
                text = "Convenience Store",
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextDark
            )
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Logo
        Box(
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(24.dp)).background(DarkGreenLogo),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Store, contentDescription = "Logo", tint = Color.White, modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Enter your mobile number to get started", fontSize = 14.sp, color = TextGray)

        Spacer(modifier = Modifier.height(40.dp))

        // 手机号输入区
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("MOBILE NUMBER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = phone,
                onValueChange = { viewModel.updatePhone(it) },
                placeholder = { Text("+1 (555) 000-0000", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = ButtonActiveGreen) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = FieldBg,
                    unfocusedContainerColor = FieldBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 验证码输入区
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("VERIFICATION CODE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = code,
                    onValueChange = { viewModel.updateCode(it) },
                    placeholder = { Text("••••••", color = TextGray) },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = ButtonActiveGreen) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = FieldBg,
                        unfocusedContainerColor = FieldBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                // 获取验证码按钮
                Button(
                    onClick = { viewModel.sendVerificationCode() },
                    enabled = isPhoneValid && !isCountingDown,
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FieldBg,
                        contentColor = ButtonActiveGreen,
                        disabledContainerColor = FieldBg,
                        disabledContentColor = TextGray
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (isCountingDown) "${countdownSeconds}s" else "Get Code",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 验证登录按钮
        Button(
            onClick = {
                viewModel.verifyAndLogin(
                    onSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            },
            enabled = isLoginEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = if (isLoginEnabled) 12.dp else 0.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = ButtonActiveGreen,
                    spotColor = ButtonActiveGreen
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonActiveGreen,
                disabledContainerColor = ButtonInactiveGreen,
                contentColor = Color.White,
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text("Verify Code", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Text("New to the store? ", color = TextGray)
            Text("Create an account", color = ButtonActiveGreen, fontWeight = FontWeight.Bold)
        }
    }
}