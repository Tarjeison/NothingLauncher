package com.tlapp.launchnothing.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val paddingSmall: Dp = 8.dp,
    val paddingMedium: Dp = 16.dp,
    val iconSize: Dp = 48.dp
)

val LocalDimensions = compositionLocalOf { Dimensions() }
