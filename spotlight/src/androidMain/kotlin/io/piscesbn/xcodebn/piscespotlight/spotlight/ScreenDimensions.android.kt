package io.piscesbn.xcodebn.piscespotlight.spotlight

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

/**
 * Android implementation using LocalConfiguration
 */
@Composable
actual fun getScreenDimensions(): ScreenDimensions {
    val configuration = LocalConfiguration.current
    return ScreenDimensions(
        widthDp = configuration.screenWidthDp.dp,
        heightDp = configuration.screenHeightDp.dp
    )
}
