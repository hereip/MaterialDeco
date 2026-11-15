package com.example.materialdeco

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.materialdeco.ui.theme.MaterialDecoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        hideSystemUi()

        setContent {
            var settings by remember { mutableStateOf(loadSettings(this)) }
            var showSettings by remember { mutableStateOf(false) }
            var screensaver by remember { mutableStateOf(false) }

            LaunchedEffect(settings) {
                saveSettings(this@MainActivity, settings)
            }

            MaterialDecoTheme(style = settings.style) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().pointerInput(showSettings, screensaver) {
                            detectTapGestures(
                                onTap = {
                                    if (screensaver) screensaver = false
                                },
                                onDoubleTap = {
                                    if (!showSettings) {
                                        screensaver = false
                                        showSettings = true
                                    }
                                }
                            )
                        }
                    ) {
                        if (showSettings) {
                            SettingsScreen(
                                settings = settings,
                                onSettingsChange = { settings = it },
                                onClose = { showSettings = false },
                                onStartScreensaver = {
                                    showSettings = false
                                    screensaver = true
                                }
                            )
                        } else {
                            BackgroundShapes(settings)
                            ClockScreen(settings, screensaver)
                        }
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
            WindowCompat.getInsetsController(window, window.decorView)
                .hide(WindowInsetsCompat.Type.systemBars())
        }, 500)
    }
}
