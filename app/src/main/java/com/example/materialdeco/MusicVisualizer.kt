package com.example.materialdeco

import android.Manifest
import android.content.pm.PackageManager
import android.media.audiofx.Visualizer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlin.math.abs

@Composable
fun MusicVisualizer(enabled: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val barColor = MaterialTheme.colorScheme.primary
    var waveform by remember { mutableStateOf(List(32) { 0 }) }
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    DisposableEffect(enabled, hasPermission) {
        if (!enabled || !hasPermission) {
            waveform = List(32) { 0 }
            onDispose { }
        } else {
            val visualizer = runCatching { Visualizer(0) }.getOrNull()
            if (visualizer == null) {
                onDispose { }
            } else {
                runCatching {
                    visualizer.captureSize = Visualizer.getCaptureSizeRange()[1]
                    visualizer.setDataCaptureListener(
                        object : Visualizer.OnDataCaptureListener {
                            override fun onWaveFormDataCapture(
                                visualizer: Visualizer,
                                bytes: ByteArray,
                                samplingRate: Int
                            ) {
                                waveform = bytes.filterIndexed { index, _ -> index % 4 == 0 }
                                    .take(32)
                                    .map { abs((it.toInt() and 0xFF) - 128) }
                            }

                            override fun onFftDataCapture(
                                visualizer: Visualizer,
                                bytes: ByteArray,
                                samplingRate: Int
                            ) = Unit
                        },
                        (Visualizer.getMaxCaptureRate() / 2).coerceAtLeast(1),
                        true,
                        false
                    )
                    visualizer.enabled = true
                }.onFailure {
                    runCatching { visualizer.release() }
                }
                onDispose {
                    runCatching { visualizer.enabled = false }
                    runCatching { visualizer.release() }
                }
            }
        }
    }

    Canvas(modifier = modifier.fillMaxWidth().height(52.dp)) {
        val count = waveform.size.coerceAtLeast(1)
        val slot = size.width / count
        waveform.forEachIndexed { index, value ->
            val normalized = (value / 128f).coerceIn(0.04f, 1f)
            val barHeight = size.height * normalized
            drawLine(
                color = barColor,
                start = Offset(index * slot + slot / 2f, (size.height - barHeight) / 2f),
                end = Offset(index * slot + slot / 2f, (size.height + barHeight) / 2f),
                strokeWidth = (slot * 0.48f).coerceAtLeast(2f),
                cap = StrokeCap.Round
            )
        }
    }
}
