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

data class AppSettings(
    val style: DecoStyle = DecoStyle.MD3,
    val shape: DecoShape = DecoShape.RANDOM,
    val palette: DecoPalette = DecoPalette.THEME,
    val shapeSize: Float = 220f
)

private const val PREFS_NAME = "material_deco_settings"

fun loadSettings(context: Context): AppSettings {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return AppSettings(
        style = runCatching { DecoStyle.valueOf(prefs.getString("style", null) ?: "") }.getOrDefault(DecoStyle.MD3),
        shape = runCatching { DecoShape.valueOf(prefs.getString("shape", null) ?: "") }.getOrDefault(DecoShape.RANDOM),
        palette = runCatching { DecoPalette.valueOf(prefs.getString("palette", null) ?: "") }.getOrDefault(DecoPalette.THEME),
        shapeSize = prefs.getFloat("shapeSize", 220f).coerceIn(100f, 360f)
    )
}

fun saveSettings(context: Context, settings: AppSettings) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        .putString("style", settings.style.name)
        .putString("shape", settings.shape.name)
        .putString("palette", settings.palette.name)
        .putFloat("shapeSize", settings.shapeSize)
        .apply()
}
