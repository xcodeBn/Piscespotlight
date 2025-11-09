package io.piscesbn.xcodebn.piscespotlight.spotlight

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import java.awt.Toolkit

/**
 * JVM/Desktop implementation using AWT Toolkit
 */
@Composable
actual fun getScreenDimensions(): ScreenDimensions {
    val density = LocalDensity.current
    val screenSize = Toolkit.getDefaultToolkit().screenSize

    return ScreenDimensions(
        widthDp = with(density) { screenSize.width.toDp() },
        heightDp = with(density) { screenSize.height.toDp() }
    )
}
