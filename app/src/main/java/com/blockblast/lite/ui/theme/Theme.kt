package com.blockblast.lite.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand colors — strictly these three for the app chrome.
val Black = Color(0xFF0A0A0A)
val DarkBlue = Color(0xFF12275C)
val White = Color(0xFFFFFFFF)
val GrayText = Color(0xFFAAB2C5)

// Piece colors.
val PieceBlue = Color(0xFF3B82F6)
val PieceGreen = Color(0xFF22C55E)
val PieceYellow = Color(0xFFFACC15)
val PieceRed = Color(0xFFEF4444)
val PiecePurple = Color(0xFFA855F7)

fun pieceColor(colorId: Int): Color = when (colorId) {
    1 -> PieceBlue
    2 -> PieceGreen
    3 -> PieceYellow
    4 -> PieceRed
    5 -> PiecePurple
    else -> PieceBlue
}

private val ColorScheme = darkColorScheme(
    primary = DarkBlue,
    secondary = PieceBlue,
    background = Black,
    surface = DarkBlue,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White
)

@Composable
fun BlockBlastLiteTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColorScheme, content = content)
}
