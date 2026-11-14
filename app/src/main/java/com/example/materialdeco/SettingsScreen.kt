package com.example.materialdeco

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(settings: AppSettings, onSettingsChange: (AppSettings) -> Unit, onClose: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Настройки", style = MaterialTheme.typography.headlineMedium)
        Text("Стиль оформления", style = MaterialTheme.typography.titleMedium)
        DecoStyle.values().forEach { option ->
            SelectableOption(option.title, settings.style == option) { onSettingsChange(settings.copy(style = option)) }
        }
        Text("Фигуры", style = MaterialTheme.typography.titleMedium)
        DecoShape.values().forEach { option ->
            SelectableOption(option.title, settings.shape == option) { onSettingsChange(settings.copy(shape = option)) }
        }
        Text("Цветовая палитра", style = MaterialTheme.typography.titleMedium)
        DecoPalette.values().forEach { option ->
            SelectableOption(option.title, settings.palette == option) { onSettingsChange(settings.copy(palette = option)) }
        }
        Text("Размер фигур: ${settings.shapeSize.toInt()} dp", style = MaterialTheme.typography.titleMedium)
        Slider(value = settings.shapeSize, onValueChange = { onSettingsChange(settings.copy(shapeSize = it)) }, valueRange = 100f..360f)
        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) { Text("Готово") }
    }
}

@Composable
private fun SelectableOption(title: String, selected: Boolean, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = onClick)
            Text(title, modifier = Modifier.padding(start = 8.dp))
        }
    }
}
