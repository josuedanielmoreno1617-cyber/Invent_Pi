package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue

data class AppColors(
    val backgroundNavy: Color,
    val cardNavy: Color,
    val textSilver: Color,
    val limeGreen: Color,
    val fieldBackground: Color,
    val textDark: Color,
    val textGray: Color
)

fun getAppColors(isDarkMode: Boolean): AppColors {
    return if (isDarkMode) {
        AppColors(
            backgroundNavy = Color(0xFF0A1F38).copy(alpha = 0.6f),
            cardNavy = Color(0xFF112B4A),
            textSilver = Color(0xFFE0E2E6),
            limeGreen = Color(0xFF98FB37),
            fieldBackground = Color(0xFFE0E2E6),
            textDark = Color(0xFF333333),
            textGray = Color(0xFF666666)
        )
    } else {
        AppColors(
            backgroundNavy = Color(0xFFE3F2FD).copy(alpha = 0.8f), // Lighter blue transparent
            cardNavy = Color.White,
            textSilver = Color(0xFF1A365D), // Dark text for light mode
            limeGreen = Color(0xFF007A33),  // Darker green for contrast
            fieldBackground = Color(0xFFF7F9FC),
            textDark = Color(0xFF333333),
            textGray = Color(0xFF666666)
        )
    }
}
