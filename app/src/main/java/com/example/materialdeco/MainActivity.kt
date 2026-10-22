package com.example.materialdeco

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        hideSystemUi()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Оборачиваем всё в Box, чтобы пузыри были на фоне
                    Box(modifier = Modifier.fillMaxSize()) {
                        // 1. Фон с пузырями
                        MaterialBubbles()

                        // 2. Часы поверх пузырей
                        ClockScreen()
                    }
                }
            }
        }
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        window.decorView.postDelayed({
            val c = WindowCompat.getInsetsController(window, window.decorView)
            c.hide(WindowInsetsCompat.Type.systemBars())
        }, 500)
    }
}

// 🫧 Компонент с пузырями
@Composable
fun MaterialBubbles() {
    // Берем красивые цвета из темы Material 3
    val colorPrimary = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    val colorSecondary = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
    val colorTertiary = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)

    // Бесконечная плавная анимация (туда-сюда)
    val infiniteTransition = rememberInfiniteTransition(label = "bubble_animation")

    // Анимация для смещения по X и Y (медленно и плавно)
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "offsetX"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = -150f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "offsetY"
    )

    // Анимация размера (дышащие пузыри)
    val sizeScale by infiniteTransition.animateFloat(
        initialValue = 150f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "sizeScale"
    )

    // Рисуем три пузыря в разных местах
    Box(modifier = Modifier.fillMaxSize()) {
        // Пузырь 1 (сверху слева)
        Box(
            modifier = Modifier
                .offset(x = (offsetX).dp, y = (offsetY).dp)
                .size(sizeScale.dp)
                .clip(CircleShape)
                .background(colorPrimary)
        )

        // Пузырь 2 (справа)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (offsetY).dp, y = (offsetX).dp)
                .size((sizeScale * 0.8f).dp)
                .clip(CircleShape)
                .background(colorSecondary)
        )

        // Пузырь 3 (снизу)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (offsetX / 2).dp, y = (offsetY / 2).dp)
                .size((sizeScale * 1.2f).dp)
                .clip(CircleShape)
                .background(colorTertiary)
        )
    }
}

@Composable
fun ClockScreen() {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale("ru"))

        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)
            delay(1000)
        }
    }

    // Занимаем весь экран, чтобы часы были по центру
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Text(
                text = currentTime,
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = currentDate,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            BatteryLevelText()
        }
    }
}

@Composable
fun BatteryLevelText() {
    val context = LocalContext.current
    var batteryPercent by remember { mutableStateOf(0) }

    // Слушаем изменения батареи
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                if (level >= 0 && scale > 0) {
                    batteryPercent = level * 100 / scale
                }
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Text(
        text = " $batteryPercent%",
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    )
}