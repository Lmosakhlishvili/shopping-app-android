package com.lmos.androidshopapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ShopColorScheme = lightColorScheme(
    primary = Color(0xFF131921),
    onPrimary = Color.White,
    secondary = Color(0xFFFF9900),
    onSecondary = Color.Black,
    background = Color(0xFFF5F6F7),
    surface = Color.White
)

@Composable
fun AndroidShopAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ShopColorScheme,
        content = content
    )
}
