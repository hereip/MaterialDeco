package com.example.materialdeco

import android.content.Context

enum class DecoStyle(val title: String) {
    LIQUID_GLASS("Liquid Glass"),
    WINDOWS_XP("Windows XP"),
    LINUX_TTY("Linux TTY"),
    MD3_LITE("MD3 Lite"),
    MD3("MD3")
}

enum class DecoShape(val title: String) {
    RANDOM("Случайные"),
    CIRCLE("Круги"),
    SQUIRCLE("Квадратики"),
    COOKIE("Печеньки")
}

enum class DecoPalette(val title: String) {
    THEME("Цвета темы"),
    OCEAN("Океан"),
    SUNSET("Закат"),
    FOREST("Лес"),
    MONOCHROME("Монохром")
}

enum class ClockFont(val title: String) {
    SYSTEM("Системный"),
    MONOSPACE("Моноширинный"),
    SERIF("С засечками"),
    CURSIVE("Рукописный")
}

data class AppSettings(
    val style: DecoStyle = DecoStyle.MD3,
    val shape: DecoShape = DecoShape.RANDOM,
    val palette: DecoPalette = DecoPalette.THEME,
    val shapeSize: Float = 220f,
    val clockFont: ClockFont = ClockFont.SYSTEM,
    val animationEnabled: Boolean = true,
    val animationSpeed: Float = 1f,
    val shapeCount: Int = 3,
    val visualizerEnabled: Boolean = false
)

private const val PREFS_NAME = "material_deco_settings"
private const val PRESETS_KEY = "user_presets"

data class UserPreset(val name: String, val settings: AppSettings)

fun loadSettings(context: Context): AppSettings {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return AppSettings(
        style = enumValueOrDefault(prefs.getString("style", null), DecoStyle.MD3),
        shape = enumValueOrDefault(prefs.getString("shape", null), DecoShape.RANDOM),
        palette = enumValueOrDefault(prefs.getString("palette", null), DecoPalette.THEME),
        shapeSize = prefs.getFloat("shapeSize", 220f).coerceIn(100f, 360f),
        clockFont = enumValueOrDefault(prefs.getString("clockFont", null), ClockFont.SYSTEM),
        animationEnabled = prefs.getBoolean("animationEnabled", true),
        animationSpeed = prefs.getFloat("animationSpeed", 1f).coerceIn(0.25f, 2.5f),
        shapeCount = prefs.getInt("shapeCount", 3).coerceIn(1, 6),
        visualizerEnabled = prefs.getBoolean("visualizerEnabled", false)
    )
}

private inline fun <reified T : Enum<T>> enumValueOrDefault(value: String?, default: T): T =
    value?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: default

fun saveSettings(context: Context, settings: AppSettings) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        .putString("style", settings.style.name)
        .putString("shape", settings.shape.name)
        .putString("palette", settings.palette.name)
        .putFloat("shapeSize", settings.shapeSize)
        .putString("clockFont", settings.clockFont.name)
        .putBoolean("animationEnabled", settings.animationEnabled)
        .putFloat("animationSpeed", settings.animationSpeed)
        .putInt("shapeCount", settings.shapeCount)
        .putBoolean("visualizerEnabled", settings.visualizerEnabled)
        .apply()
}

fun loadPresets(context: Context): List<UserPreset> {
    val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getStringSet(PRESETS_KEY, emptySet()).orEmpty()
    return stored.mapNotNull { line ->
        val parts = line.split('|')
        if (parts.size != 10) return@mapNotNull null
        runCatching {
            UserPreset(
                name = parts[0],
                settings = AppSettings(
                    style = enumValueOf(parts[1]),
                    shape = enumValueOf(parts[2]),
                    palette = enumValueOf(parts[3]),
                    shapeSize = parts[4].toFloat().coerceIn(100f, 360f),
                    clockFont = enumValueOf(parts[5]),
                    animationEnabled = parts[6].toBoolean(),
                    animationSpeed = parts[7].toFloat().coerceIn(0.25f, 2.5f),
                    shapeCount = parts[8].toInt().coerceIn(1, 6),
                    visualizerEnabled = parts[9].toBoolean()
                )
            )
        }.getOrNull()
    }
}

fun savePreset(context: Context, name: String, settings: AppSettings) {
    val safeName = name.trim().replace("|", " ").take(32)
    if (safeName.isBlank()) return
    val preset = listOf(
        safeName, settings.style.name, settings.shape.name, settings.palette.name,
        settings.shapeSize.toString(), settings.clockFont.name, settings.animationEnabled.toString(),
        settings.animationSpeed.toString(), settings.shapeCount.toString(), settings.visualizerEnabled.toString()
    ).joinToString("|")
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val updated = prefs.getStringSet(PRESETS_KEY, emptySet()).orEmpty().toMutableSet()
    updated.removeAll { it.substringBefore('|') == safeName }
    updated.add(preset)
    prefs.edit().putStringSet(PRESETS_KEY, updated).apply()
}

fun deletePreset(context: Context, name: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val updated = prefs.getStringSet(PRESETS_KEY, emptySet()).orEmpty()
        .filterNot { it.substringBefore('|') == name }.toSet()
    prefs.edit().putStringSet(PRESETS_KEY, updated).apply()
}
