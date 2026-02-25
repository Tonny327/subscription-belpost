package com.belpost.subscription.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Палитра, приближенная к сайту Белпочты: белый фон и синий акцент
private val BelpostLightColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF2661FD),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF6E8FFF),
    onPrimaryContainer = Color.White,

    secondary = Color(0xFF007AFF),
    onSecondary = Color.White,

    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF212121),

    surface = Color.White,
    onSurface = Color(0xFF212121),

    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF616161),

    outline = Color(0xFFBDBDBD)
)

@Composable
fun BelpostTheme(
    // Всегда светлая тема, независимо от системной
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BelpostLightColorScheme,
        typography = Typography,
        content = content
    )
}


