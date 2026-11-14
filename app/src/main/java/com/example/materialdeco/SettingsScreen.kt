package com.example.materialdeco

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlin.random.Random

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onSettingsChange: (AppSettings) -> Unit,
    onClose: () -> Unit,
    onStartScreensaver: () -> Unit
) {
    val context = LocalContext.current
    var presetName by remember { mutableStateOf("") }
    var presets by remember { mutableStateOf(loadPresets(context)) }
    val audioPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        onSettingsChange(settings.copy(visualizerEnabled = granted))
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Настройки", style = MaterialTheme.typography.headlineMedium)

        Text("Стиль оформления", style = MaterialTheme.typography.titleMedium)
        DecoStyle.values().forEach { option ->
            SelectableOption(option.title, settings.style == option) {
                onSettingsChange(settings.copy(style = option))
            }
        }

        Text("Фигуры", style = MaterialTheme.typography.titleMedium)
        DecoShape.values().forEach { option ->
            SelectableOption(option.title, settings.shape == option) {
                onSettingsChange(settings.copy(shape = option))
            }
        }

        Text("Цветовая палитра", style = MaterialTheme.typography.titleMedium)
        DecoPalette.values().forEach { option ->
            SelectableOption(option.title, settings.palette == option) {
                onSettingsChange(settings.copy(palette = option))
            }
        }

        Text("Шрифт часов", style = MaterialTheme.typography.titleMedium)
        ClockFont.values().forEach { option ->
            SelectableOption(option.title, settings.clockFont == option) {
                onSettingsChange(settings.copy(clockFont = option))
            }
        }

        HorizontalDivider()
        Text("Анимации", style = MaterialTheme.typography.titleMedium)
        SettingSwitch(
            title = "Включить анимации",
            checked = settings.animationEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(animationEnabled = it)) }
        )
        Text("Скорость: ${String.format("%.2f", settings.animationSpeed)}×")
        Slider(
            value = settings.animationSpeed,
            onValueChange = { onSettingsChange(settings.copy(animationSpeed = it)) },
            valueRange = 0.25f..2.5f
        )
        Text("Количество фигур: ${settings.shapeCount}")
        Slider(
            value = settings.shapeCount.toFloat(),
            onValueChange = { onSettingsChange(settings.copy(shapeCount = it.toInt().coerceIn(1, 6))) },
            valueRange = 1f..6f,
            steps = 4
        )
        Text("Размер фигур: ${settings.shapeSize.toInt()} dp")
        Slider(
            value = settings.shapeSize,
            onValueChange = { onSettingsChange(settings.copy(shapeSize = it)) },
            valueRange = 100f..360f
        )

        HorizontalDivider()
        Text("Музыка", style = MaterialTheme.typography.titleMedium)
        SettingSwitch(
            title = "Music Visualizer",
            checked = settings.visualizerEnabled,
            onCheckedChange = { enabled ->
                if (!enabled) {
                    onSettingsChange(settings.copy(visualizerEnabled = false))
                } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    onSettingsChange(settings.copy(visualizerEnabled = true))
                } else {
                    audioPermission.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        )
        Text(
            "Для анализа звука Android запросит разрешение на доступ к микрофону. Приложение использует системный Audio Visualizer.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        HorizontalDivider()
        Text("Мои пресеты", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = presetName,
            onValueChange = { presetName = it.take(32) },
            label = { Text("Название пресета") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                savePreset(context, presetName, settings)
                presets = loadPresets(context)
                presetName = ""
            },
            enabled = presetName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Сохранить текущий пресет") }

        if (presets.isEmpty()) {
            Text("Пока нет сохранённых пресетов.", style = MaterialTheme.typography.bodySmall)
        }
        presets.forEach { preset ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(preset.name, modifier = Modifier.weight(1f))
                    TextButton(onClick = { onSettingsChange(preset.settings) }) { Text("Применить") }
                    TextButton(onClick = {
                        deletePreset(context, preset.name)
                        presets = loadPresets(context)
                    }) { Text("Удалить") }
                }
            }
        }

        HorizontalDivider()
        OutlinedButton(onClick = onStartScreensaver, modifier = Modifier.fillMaxWidth()) {
            Text("Запустить режим заставки")
        }
        OutlinedButton(
            onClick = {
                val randomSettings = settings.copy(
                    style = DecoStyle.values().random(),
                    shape = DecoShape.values().filterNot { it == DecoShape.RANDOM }.random(),
                    palette = DecoPalette.values().random(),
                    shapeSize = Random.nextInt(130, 341).toFloat(),
                    clockFont = ClockFont.values().random(),
                    animationEnabled = Random.nextBoolean(),
                    animationSpeed = Random.nextInt(25, 251) / 100f,
                    shapeCount = Random.nextInt(1, 7)
                )
                onSettingsChange(randomSettings)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🎲 Randomize")
        }
        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text("Готово")
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SettingSwitch(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SelectableOption(title: String, selected: Boolean, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = onClick)
            Text(title, modifier = Modifier.padding(start = 8.dp))
        }
    }
}
