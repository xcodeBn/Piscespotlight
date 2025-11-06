package io.piscesbn.xcodebn.piscespotlight.spotlight

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

/**
 * Platform-specific screen dimensions provider
 */
data class ScreenDimensions(
    val widthDp: Dp,
    val heightDp: Dp
)

/**
 * Get current screen dimensions in a platform-specific way
 */
@Composable
expect fun getScreenDimensions(): ScreenDimensions
