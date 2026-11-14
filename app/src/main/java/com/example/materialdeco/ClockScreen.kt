package com.example.materialdeco

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockScreen(style: DecoStyle) {
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

    val font = if (style == DecoStyle.LINUX_TTY) FontFamily.Monospace else FontFamily.Default
    Box(Modifier.fillMaxSize().systemBarsPadding(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(currentTime, fontSize = 120.sp, fontWeight = FontWeight.Bold, fontFamily = font, color = MaterialTheme.colorScheme.onBackground)
            Text(currentDate, fontSize = 22.sp, fontFamily = font, color = MaterialTheme.colorScheme.onSurfaceVariant)
            BatteryLevelText(style)
        }
    }
}

@Composable
private fun BatteryLevelText(style: DecoStyle) {
    val context = LocalContext.current
    var batteryPercent by remember { mutableIntStateOf(0) }
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                if (level >= 0 && scale > 0) batteryPercent = level * 100 / scale
            }
        }
        val sticky = context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        receiver.onReceive(context, sticky)
        onDispose { context.unregisterReceiver(receiver) }
    }
    Text(if (style == DecoStyle.LINUX_TTY) "[BATTERY: $batteryPercent%]" else "$batteryPercent%", fontSize = 16.sp, fontFamily = if (style == DecoStyle.LINUX_TTY) FontFamily.Monospace else FontFamily.Default, color = MaterialTheme.colorScheme.onSurfaceVariant)
}
