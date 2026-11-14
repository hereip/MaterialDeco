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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.LayoutDirection
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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
                    Box(modifier = Modifier.fillMaxSize()) {
                        MaterialBubbles()
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

// Фигурный контур печеньки в стиле Material 3.
private object CookieShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): Outline {
        val path = Path()
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val baseRadius = minOf(size.width, size.height) / 2f
        val bumps = 12
        val steps = 240

        for (step in 0..steps) {
            val angle = step.toFloat() / steps * (2f * PI.toFloat())
            val radius = baseRadius * (0.88f + 0.12f * cos(bumps * angle))
            val x = centerX + radius * cos(angle)
            val y = centerY + radius * sin(angle)
            if (step == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return Outline.Generic(path)
    }
}

// Три фигуры каждый запуск выбираются случайно: круг, скруглённый квадрат или печенька.
@Composable
fun MaterialBubbles() {
    val colorPrimary = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    val colorSecondary = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
    val colorTertiary = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)

    val shapes = remember {
        List(3) {
            listOf(
                CircleShape,
                RoundedCornerShape(28.dp),
                CookieShape
            ).random()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "shape_animation")

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

    val sizeScale by infiniteTransition.animateFloat(
        initialValue = 150f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "sizeScale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset(x = offsetX.dp, y = offsetY.dp)
                .size(sizeScale.dp)
                .clip(shapes[0])
                .background(colorPrimary)
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = offsetY.dp, y = offsetX.dp)
                .size((sizeScale * 0.8f).dp)
                .clip(shapes[1])
                .background(colorSecondary)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (offsetX / 2).dp, y = (offsetY / 2).dp)
                .size((sizeScale * 1.2f).dp)
                .clip(shapes[2])
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