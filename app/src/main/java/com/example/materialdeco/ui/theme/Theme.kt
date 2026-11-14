package com.example.materialdeco.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.materialdeco.DecoStyle

private val DarkColorScheme = darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)
private val LightColorScheme = lightColorScheme(primary = Purple40, secondary = PurpleGrey40, tertiary = Pink40)

@Composable
fun MaterialDecoTheme(
    style: DecoStyle = DecoStyle.MD3,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val scheme = when (style) {
        DecoStyle.MD3 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else if (darkTheme) DarkColorScheme else LightColorScheme
        DecoStyle.MD3_LITE -> if (darkTheme) {
            darkColorScheme(primary = Color(0xFFB8C5FF), secondary = Color(0xFFB9C7D8), tertiary = Color(0xFFD0BCFF), background = Color(0xFF101114), surface = Color(0xFF17191D))
        } else {
            lightColorScheme(primary = Color(0xFF405A9A), secondary = Color(0xFF526174), tertiary = Color(0xFF66558D), background = Color(0xFFF8F9FC), surface = Color(0xFFF8F9FC))
        }
        DecoStyle.LIQUID_GLASS -> if (darkTheme) {
            darkColorScheme(primary = Color(0xFFB5D8FF), secondary = Color(0xFFC7C5FF), tertiary = Color(0xFF9DE9E0), background = Color(0xFF101923), surface = Color(0xFF182633))
        } else {
            lightColorScheme(primary = Color(0xFF356B91), secondary = Color(0xFF655D9B), tertiary = Color(0xFF287D78), background = Color(0xFFEAF4FA), surface = Color(0xFFF3F9FC))
        }
        DecoStyle.WINDOWS_XP -> if (darkTheme) {
            darkColorScheme(primary = Color(0xFF78B9FF), secondary = Color(0xFF9BCBFF), tertiary = Color(0xFFFFD36E), background = Color(0xFF102B50), surface = Color(0xFF183D69))
        } else {
            lightColorScheme(primary = Color(0xFF1769AA), secondary = Color(0xFF3D83C6), tertiary = Color(0xFFD99B28), background = Color(0xFFEAF3FF), surface = Color(0xFFF5F9FF))
        }
        DecoStyle.LINUX_TTY -> darkColorScheme(
            primary = Color(0xFF00FF66), secondary = Color(0xFF7CFFB0), tertiary = Color(0xFFB6FF00),
            background = Color(0xFF050805), surface = Color(0xFF0B110C),
            onBackground = Color(0xFF00FF66), onSurface = Color(0xFF00FF66), onSurfaceVariant = Color(0xFF9BE6AE)
        )
    }
    MaterialTheme(colorScheme = scheme, typography = Typography, content = content)
}
