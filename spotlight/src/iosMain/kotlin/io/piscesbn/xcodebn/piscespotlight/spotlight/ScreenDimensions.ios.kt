package io.piscesbn.xcodebn.piscespotlight.spotlight

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

/**
 * iOS implementation using LocalWindowInfo
 */
@Composable
actual fun getScreenDimensions(): ScreenDimensions {
    val containerSize = LocalWindowInfo.current.containerSize
    return ScreenDimensions(
        widthDp = containerSize.width.dp,
        heightDp = containerSize.height.dp
    )
}
