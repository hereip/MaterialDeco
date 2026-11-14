package com.example.materialdeco

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private object CookieShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: androidx.compose.ui.unit.Density): Outline {
        val path = Path()
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = minOf(size.width, size.height) / 2f
        for (step in 0..240) {
            val angle = step / 240f * 2f * PI.toFloat()
            val r = radius * (0.88f + 0.12f * cos(12f * angle))
            val x = cx + r * cos(angle)
            val y = cy + r * sin(angle)
            if (step == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return Outline.Generic(path)
    }
}

@Composable
fun BackgroundShapes(settings: AppSettings) {
    val scheme = MaterialTheme.colorScheme
    val colors = when (settings.palette) {
        DecoPalette.THEME -> listOf(scheme.primary, scheme.secondary, scheme.tertiary)
        DecoPalette.OCEAN -> listOf(Color(0xFF56B4E9), Color(0xFF287DAB), Color(0xFF8AD9D0))
        DecoPalette.SUNSET -> listOf(Color(0xFFFF9B73), Color(0xFFE66A91), Color(0xFFFFCA75))
        DecoPalette.FOREST -> listOf(Color(0xFF78B889), Color(0xFF397B61), Color(0xFFB6C978))
        DecoPalette.MONOCHROME -> listOf(Color(0xFF9AA0AA), Color(0xFF69717E), Color(0xFFC5CAD2))
    }
    val shapes = remember(settings.shape, settings.shapeCount) {
        val options = listOf(CircleShape, RoundedCornerShape(28.dp), CookieShape)
        when (settings.shape) {
            DecoShape.CIRCLE -> List(settings.shapeCount) { CircleShape }
            DecoShape.SQUIRCLE -> List(settings.shapeCount) { RoundedCornerShape(28.dp) }
            DecoShape.COOKIE -> List(settings.shapeCount) { CookieShape }
            DecoShape.RANDOM -> List(settings.shapeCount) { options.random() }
        }
    }
    val transition = rememberInfiniteTransition(label = "background_shapes")
    val duration = (16000 / settings.animationSpeed).toInt().coerceAtLeast(1000)
    val driftAnimation by transition.animateFloat(
        initialValue = -90f,
        targetValue = 170f,
        animationSpec = infiniteRepeatable(tween(duration, easing = LinearEasing), RepeatMode.Reverse),
        label = "drift"
    )
    val scaleAnimation by transition.animateFloat(
        initialValue = settings.shapeSize * 0.72f,
        targetValue = settings.shapeSize * 1.28f,
        animationSpec = infiniteRepeatable(tween((11000 / settings.animationSpeed).toInt().coerceAtLeast(1000), easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )
    val drift = if (settings.animationEnabled) driftAnimation else 0f
    val scale = if (settings.animationEnabled) scaleAnimation else settings.shapeSize
    val alpha = if (settings.style == DecoStyle.LIQUID_GLASS) 0.22f else 0.15f
    val positions = listOf(Alignment.TopStart, Alignment.CenterEnd, Alignment.BottomStart, Alignment.TopEnd, Alignment.CenterStart, Alignment.BottomEnd)

    Box(Modifier.fillMaxSize()) {
        shapes.forEachIndexed { index, shape ->
            val direction = if (index % 2 == 0) 1f else -1f
            val sizeFactor = 0.72f + (index % 3) * 0.18f
            Box(
                Modifier.align(positions[index % positions.size])
                    .offset(x = (drift * direction * (0.35f + index * 0.08f)).dp, y = (-drift * direction * 0.45f).dp)
                    .size((scale * sizeFactor).dp)
                    .clip(shape)
                    .background(colors[index % colors.size].copy(alpha = alpha))
            )
        }
    }
}
