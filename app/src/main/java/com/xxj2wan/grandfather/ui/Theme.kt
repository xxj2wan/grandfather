package com.xxj2wan.grandfather.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

object Palette {
    val Background = Color(0xFFFFF8E7)
    val CellDefault = Color.White
    val CellBorder = Color(0xFF212121)
    val Text = Color(0xFF111111)
    val Selected = Color(0xFF1565C0)
    val Matched = Color(0xFF2E7D32)
    val Wrong = Color(0xFFC62828)
    val Reveal = Color(0xFFFFD54F)
    val TimerWarning = Color(0xFFC62828)
}

/**
 * 시스템 글꼴 크기 설정과 무관하게 dp 기준으로 고정된 글자 크기.
 * 어르신 폰은 글꼴이 '아주 크게'로 되어 있는 경우가 많아, sp를 그대로 쓰면 칸을 넘친다.
 * 화면 자체를 이미 크게 설계했으므로 여기서는 고정 크기를 쓴다.
 */
@Composable
fun Dp.fixedSp(): TextUnit = with(LocalDensity.current) { this@fixedSp.toSp() }

@Composable
fun GrandfatherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Palette.Selected,
            background = Palette.Background,
            surface = Palette.Background,
            onBackground = Palette.Text,
            onSurface = Palette.Text,
        ),
        content = content,
    )
}
