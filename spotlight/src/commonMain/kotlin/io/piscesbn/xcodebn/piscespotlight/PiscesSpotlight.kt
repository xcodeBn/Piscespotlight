package io.piscesbn.xcodebn.piscespotlight.spotlight

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PiscesSpotlight - A cross-platform spotlight tutorial system for Compose Multiplatform.
 *
 * This library provides a robust solution for implementing interactive tutorials that guide users
 * through your application by highlighting specific UI components and displaying contextual tooltips.
 *
 * ## Features
 *
 * - Cross-platform compatibility (Android, iOS, Desktop, Web)
 * - Flat API structure using CompositionLocal
 * - Support for multiple independent tutorials
 * - Type-safe or string-based target system
 * - Smart tooltip positioning with automatic edge detection
 * - State-managed tutorial progression
 * - Customizable tooltip positions
 *
 * ## Basic Usage
 *
 * ```kotlin
 * // 1. Define spotlight targets for your UI components
 * data object LoginButton : SpotlightTarget
 * data object SignupButton : SpotlightTarget
 *
 * // 2. Wrap your application with PiscesSpotlightContainer
 * PiscesSpotlightContainer(
 *     tutorials = listOf(
 *         PiscesTutorialConfig(
 *             id = "onboarding",
 *             steps = listOf(
 *                 SpotlightStep(
 *                     LoginButton,
 *                     "Login",
 *                     "Tap here to login to your account"
 *                 ),
 *                 SpotlightStep(
 *                     SignupButton,
 *                     "Sign Up",
 *                     "Or create a new account"
 *                 )
 *             )
 *         )
 *     ),
 *     onTutorialComplete = { tutorialId ->
 *         println("Tutorial $tutorialId completed")
 *     }
 * ) {
 *     // Your application content
 *     MyApp()
 * }
 *
 * // 3. Mark UI components as spotlight targets
 * Button(
 *     onClick = { /* login logic */ },
 *     modifier = Modifier.piscesSpotlightTarget(LoginButton)
 * ) {
 *     Text("Login")
 * }
 * ```
 *
 * @see PiscesSpotlightContainer
 * @see SpotlightTarget
 * @see SpotlightStep
 * @see PiscesTutorialConfig
 */

/**
 * Configuration for a tutorial sequence.
 *
 * This data class defines a complete tutorial with its identifier, steps, and conditions
 * for when it should be displayed. Multiple tutorial configurations can be registered
 * simultaneously, allowing for context-sensitive tutorials.
 *
 * @property id Unique identifier for this tutorial. Used to start, track, and complete tutorials.
 * @property steps Ordered list of [SpotlightStep] instances that make up this tutorial sequence.
 * @property enabled Whether this tutorial is currently enabled. Disabled tutorials will not
 *                   auto-start or be considered active. Defaults to true.
 * @property screenPredicate Lambda predicate evaluated to determine if this tutorial should
 *                           be shown on the current screen or navigation state. Defaults to
 *                           always true. Useful for screen-specific tutorials.
 *
 * Example:
 * ```kotlin
 * PiscesTutorialConfig(
 *     id = "main_features",
 *     steps = listOf(
 *         SpotlightStep(Feature1Target, "Feature 1", "Description"),
 *         SpotlightStep(Feature2Target, "Feature 2", "Description")
 *     ),
 *     enabled = !userHasSeenTutorial,
 *     screenPredicate = { currentScreen == Screen.Main }
 * )
 * ```
 *
 * @see SpotlightStep
 * @see PiscesSpotlightContainer
 */
data class PiscesTutorialConfig(
    val id: String,
    val steps: List<SpotlightStep>,
    val enabled: Boolean = true,
    val screenPredicate: () -> Boolean = { true }
)

/**
 * Manages the state and lifecycle of spotlight tutorials.
 *
 * This class is responsible for:
 * - Tracking registered tutorials and their configurations
 * - Managing target component positions
 * - Controlling tutorial progression (current tutorial, current step)
 * - Coordinating tutorial start, navigation, and completion
 *
 * The state is backed by Compose observable collections ([mutableStateMapOf], [mutableStateListOf])
 * to ensure proper recomposition when state changes occur.
 *
 * Typically, you should use [rememberPiscesTutorialState] to create and manage instances of this class,
 * or pass a custom instance to [PiscesSpotlightContainer] if you need external control.
 *
 * @see rememberPiscesTutorialState
 * @see PiscesSpotlightContainer
 * @see PiscesTutorialController
 */
class PiscesTutorialState {
    private val targetPositions: SnapshotStateMap<SpotlightTarget, Rect> = mutableStateMapOf()
    private val tutorialConfigs: SnapshotStateList<PiscesTutorialConfig> = mutableStateListOf()

    /**
     * The ID of the currently active tutorial, or null if no tutorial is running.
     */
    var currentTutorialId by mutableStateOf<String?>(null)
        private set

    /**
     * The zero-based index of the current step within the active tutorial.
     */
    var currentStepIndex by mutableStateOf(0)
        private set

    /**
     * Indicates whether the tutorial system is ready to display the spotlight.
     * Becomes true after target components have been positioned.
     */
    var isReady by mutableStateOf(false)
        private set

    /**
     * Indicates whether a tutorial is in the process of completing.
     * Used to trigger exit animations.
     */
    var isCompleting by mutableStateOf(false)
        private set

    /**
     * Registers a tutorial configuration with this state manager.
     *
     * @param config The [PiscesTutorialConfig] to register
     */
    fun registerTutorial(config: PiscesTutorialConfig) {
        tutorialConfigs.add(config)
    }

    /**
     * Removes all registered tutorial configurations.
     */
    fun clearTutorials() {
        tutorialConfigs.clear()
    }

    /**
     * Updates the screen position of a target component.
     *
     * This method is called automatically by the [piscesSpotlightTarget] modifier
     * when components are laid out.
     *
     * @param target The [SpotlightTarget] to update
     * @param rect The bounding rectangle of the target component
     */
    fun updateTargetPosition(target: SpotlightTarget, rect: Rect) {
        targetPositions[target] = rect
    }

    /**
     * Retrieves the screen position of a target component.
     *
     * @param target The [SpotlightTarget] to query
     * @return The bounding rectangle of the target, or null if not yet positioned
     */
    fun getTargetPosition(target: SpotlightTarget): Rect? = targetPositions[target]

    /**
     * Returns a snapshot of all registered target positions.
     *
     * @return An immutable map of all target positions
     */
    fun getAllTargetPositions(): Map<SpotlightTarget, Rect> = targetPositions.toMap()

    /**
     * Retrieves the currently active tutorial configuration.
     *
     * A tutorial is considered active if it matches the current tutorial ID,
     * is enabled, and its screen predicate returns true.
     *
     * @return The active [PiscesTutorialConfig], or null if no tutorial is active
     */
    fun getActiveTutorial(): PiscesTutorialConfig? {
        return tutorialConfigs.firstOrNull { config ->
            config.enabled && config.screenPredicate() && currentTutorialId == config.id
        }
    }

    /**
     * Retrieves the current step of the active tutorial.
     *
     * @return The current [SpotlightStep], or null if no tutorial is active or step index is invalid
     */
    fun getCurrentStep(): SpotlightStep? {
        val tutorial = getActiveTutorial() ?: return null
        return tutorial.steps.getOrNull(currentStepIndex)
    }

    /**
     * Determines whether the tutorial spotlight should be visible.
     *
     * @return true if a tutorial is active, ready, and not completing
     */
    fun shouldShowTutorial(): Boolean {
        return currentTutorialId != null && isReady && !isCompleting
    }

    /**
     * Starts a tutorial by its ID.
     *
     * This suspend function waits for the first target component to be positioned before
     * marking the tutorial as ready. It polls for up to 3 seconds for the target to become
     * available.
     *
     * @param tutorialId The ID of the tutorial to start
     */
    suspend fun startTutorial(tutorialId: String) {
        currentTutorialId = tutorialId
        currentStepIndex = 0
        isReady = false
        isCompleting = false

        // Wait for the first target to be registered instead of fixed delay
        val tutorial = tutorialConfigs.firstOrNull { it.id == tutorialId }
        val firstTarget = tutorial?.steps?.firstOrNull()?.targetKey

        if (firstTarget != null) {
            // Poll until the target is positioned (max 3 seconds timeout)
            var attempts = 0
            while (getTargetPosition(firstTarget) == null && attempts < 60) {
                delay(50)
                attempts++
            }
        } else {
            // Fallback to short delay if no steps exist
            delay(100)
        }

        isReady = true
    }

    /**
     * Advances to the next step in the tutorial or completes it.
     *
     * If there are more steps remaining, advances to the next step. If the current step
     * is the last step, marks the tutorial as completing, plays exit animation, resets
     * state, and invokes the completion callback.
     *
     * @param onComplete Callback invoked with the tutorial ID when the tutorial completes
     */
    suspend fun nextStep(onComplete: (String) -> Unit) {
        val tutorial = getActiveTutorial() ?: return

        if (currentStepIndex < tutorial.steps.size - 1) {
            currentStepIndex++
        } else {
            // Tutorial complete - trigger exit animation
            isCompleting = true
            val completedId = currentTutorialId

            // Allow exit animation to play
            delay(300)

            // Reset state and notify completion
            reset()
            completedId?.let { onComplete(it) }
        }
    }

    /**
     * Reset the current tutorial state
     * Note: This only resets the active tutorial, not registered tutorials or target positions
     */
    fun reset() {
        currentTutorialId = null
        currentStepIndex = 0
        isReady = false
        isCompleting = false
    }

    /**
     * Complete reset - clears everything including tutorials and target positions
     * Use this when you need to completely reinitialize the system
     */
    fun resetAll() {
        reset()
        tutorialConfigs.clear()
        targetPositions.clear()
    }
}

/**
 * Composition Local for Tutorial State
 */
val LocalPiscesTutorialState = compositionLocalOf<PiscesTutorialState> {
    error("PiscesTutorialState not provided - did you forget to wrap your content with PiscesSpotlightContainer?")
}


/**
 * Main PiscesSpotlight container - wrap your entire app content with this
 *
 * @param tutorials List of tutorial configurations
 * @param onTutorialComplete Callback when a tutorial completes (receives tutorial ID)
 * @param autoStart Whether to automatically start the first enabled tutorial (default: true)
 * @param content Your app content
 */
@Composable
fun PiscesSpotlightContainer(
    state: PiscesTutorialState = rememberPiscesTutorialState(),
    tutorials: List<PiscesTutorialConfig>,
    onTutorialComplete: (String) -> Unit,
    autoStart: Boolean = true,
    content: @Composable () -> Unit
) {
    val tutorialState = state
    val coroutineScope = rememberCoroutineScope()

    // Register tutorials
    LaunchedEffect(tutorials, autoStart) {
        tutorialState.clearTutorials()
        tutorials.forEach { tutorialState.registerTutorial(it) }

        // Auto-start first enabled tutorial if requested
        if (autoStart) {
            val firstEnabled = tutorials.firstOrNull { it.enabled && it.screenPredicate() }
            firstEnabled?.let { tutorialState.startTutorial(it.id) }
        }
    }

    CompositionLocalProvider(LocalPiscesTutorialState provides tutorialState) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Original content
            content()

            // Spotlight overlay
            val activeTutorial = tutorialState.getActiveTutorial()
            val currentStep = tutorialState.getCurrentStep()

            AnimatedVisibility(
                visible = tutorialState.shouldShowTutorial() && activeTutorial != null && currentStep != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (currentStep != null) {
                    val targetRect = tutorialState.getTargetPosition(currentStep.targetKey)

                    if (targetRect != null && activeTutorial != null) {
                        PiscesSpotlightOverlay(
                            targetRect = targetRect,
                            step = currentStep,
                            currentStepIndex = tutorialState.currentStepIndex,
                            totalSteps = activeTutorial.steps.size,
                            onNext = {
                                coroutineScope.launch {
                                    tutorialState.nextStep(onTutorialComplete)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun rememberPiscesTutorialState(): PiscesTutorialState {
    return remember { PiscesTutorialState() }
}

/**
 * Mark a component as a spotlight target (type-safe version)
 *
 * @param target The spotlight target (usually a data object implementing SpotlightTarget)
 */
@Composable
fun Modifier.piscesSpotlightTarget(target: SpotlightTarget): Modifier {
    return this.composed {
        val tutorialState = LocalPiscesTutorialState.current

        this.onGloballyPositioned { coordinates ->
            val rect = Rect(
                offset = Offset(
                    coordinates.positionInWindow().x,
                    coordinates.positionInWindow().y
                ),
                size = Size(
                    coordinates.size.width.toFloat(),
                    coordinates.size.height.toFloat()
                )
            )
            tutorialState.updateTargetPosition(target, rect)
        }
    }
}

/**
 * Mark a component as a spotlight target (string version)
 * Convenience overload for string-based targets
 *
 * @param key The string identifier for this target
 */
@Composable
fun Modifier.piscesSpotlightTarget(key: String): Modifier {
    return piscesSpotlightTarget(StringTarget(key))
}

// Utility function to make Modifier.composed work
@Composable
private fun Modifier.composed(
    factory: @Composable Modifier.() -> Modifier
): Modifier {
    return factory()
}

/**
 * Programmatically control tutorials
 * Use this to manually start/stop tutorials
 */
@Composable
fun rememberPiscesTutorialController(): PiscesTutorialController {
    val tutorialState = LocalPiscesTutorialState.current
    return remember { PiscesTutorialController(tutorialState) }
}

class PiscesTutorialController(
    private val state: PiscesTutorialState
) {
    suspend fun startTutorial(tutorialId: String) {
        state.startTutorial(tutorialId)
    }

    fun stopTutorial() {
        state.reset()
    }

    fun getTargetPositions(): Map<SpotlightTarget, Rect> {
        return state.getAllTargetPositions()
    }
}

/**
 * Spotlight overlay rendering with smart tooltip positioning
 */
@Composable
private fun PiscesSpotlightOverlay(
    targetRect: Rect,
    step: SpotlightStep,
    currentStepIndex: Int,
    totalSteps: Int,
    onNext: () -> Unit
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = with(density) { maxWidth.toPx() }
        val screenHeight = with(density) { maxHeight.toPx() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1000f)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { /* Block clicks */ }
        ) {
            // Draw dimmed overlay with spotlight cutout
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = 0.99f)
            ) {
                val padding = 8.dp.toPx()
                val cornerRadius = 12.dp.toPx()

                // Create path for the entire screen
                val overlayPath = Path().apply {
                    addRect(
                        Rect(
                            offset = Offset.Zero,
                            size = Size(size.width, size.height)
                        )
                    )
                }

                // Create path for the spotlight cutout
                val spotlightPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = targetRect.left - padding,
                            top = targetRect.top - padding,
                            right = targetRect.right + padding,
                            bottom = targetRect.bottom + padding,
                            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                        )
                    )
                }

                // Subtract spotlight from overlay
                val finalPath = Path().apply {
                    op(overlayPath, spotlightPath, PathOperation.Difference)
                }

                // Draw the dim overlay
                drawPath(
                    path = finalPath,
                    color = Color.Black.copy(alpha = 0.75f)
                )

                // Draw border around spotlight
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(
                        targetRect.left - padding,
                        targetRect.top - padding
                    ),
                    size = Size(
                        targetRect.width + (padding * 2),
                        targetRect.height + (padding * 2)
                    ),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Calculate tooltip position once with fixed size estimates
            val tooltipOffset = calculatePiscesTooltipOffset(
                targetRect = targetRect,
                tooltipPosition = step.tooltipPosition,
                density = density,
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                tooltipWidthDp = step.tooltipWidth,
                tooltipHeightDp = step.tooltipHeight
            )

            Card(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            tooltipOffset.x.toInt(),
                            tooltipOffset.y.toInt()
                        )
                    }
                    .width(step.tooltipWidth)
                    .height(step.tooltipHeight),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        text = "${currentStepIndex + 1}/$totalSteps",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onNext,
                        modifier = Modifier.padding(start = 0.dp)
                    ) {
                        Text(if (currentStepIndex < totalSteps - 1) "Next" else "Got it!")
                    }
                }
            }
        }
    }
}

/**
 * Smart tooltip positioning that auto-detects screen edges and places tooltip optimally
 */
private fun calculatePiscesTooltipOffset(
    targetRect: Rect,
    tooltipPosition: TooltipPosition,
    density: Density,
    screenWidth: Float,
    screenHeight: Float,
    tooltipWidthDp: androidx.compose.ui.unit.Dp,
    tooltipHeightDp: androidx.compose.ui.unit.Dp
): Offset {
    // Use fixed size estimates for position calculation
    val tooltipWidth = with(density) { tooltipWidthDp.toPx() }
    val tooltipHeight = with(density) { tooltipHeightDp.toPx() }
    val margin = with(density) { 16.dp.toPx() }
    val edgePadding = with(density) { 16.dp.toPx() }

    // Calculate available space in all directions
    val spaceAbove = targetRect.top - margin - tooltipHeight
    val spaceBelow = screenHeight - targetRect.bottom - margin - tooltipHeight
    val spaceLeft = targetRect.left - margin - tooltipWidth
    val spaceRight = screenWidth - targetRect.right - margin - tooltipWidth

    // Determine best position based on available space
    val bestPosition = determineBestPosition(
        preferredPosition = tooltipPosition,
        spaceAbove = spaceAbove,
        spaceBelow = spaceBelow,
        spaceLeft = spaceLeft,
        spaceRight = spaceRight
    )

    // Calculate position based on best available spot
    val offset = when (bestPosition) {
        TooltipPosition.Bottom -> Offset(
            x = calculateCenteredX(targetRect, tooltipWidth, screenWidth, edgePadding),
            y = targetRect.bottom + margin
        )
        TooltipPosition.Top -> Offset(
            x = calculateCenteredX(targetRect, tooltipWidth, screenWidth, edgePadding),
            y = (targetRect.top - margin - tooltipHeight).coerceAtLeast(edgePadding)
        )
        TooltipPosition.Left -> Offset(
            x = (targetRect.left - tooltipWidth - margin).coerceAtLeast(edgePadding),
            y = calculateCenteredY(targetRect, tooltipHeight, screenHeight, edgePadding)
        )
        TooltipPosition.Right -> Offset(
            x = (targetRect.right + margin).coerceAtMost(screenWidth - tooltipWidth - edgePadding),
            y = calculateCenteredY(targetRect, tooltipHeight, screenHeight, edgePadding)
        )
    }

    // Final safety check: ensure tooltip is fully on screen
    return Offset(
        x = offset.x.coerceIn(edgePadding, screenWidth - tooltipWidth - edgePadding),
        y = offset.y.coerceIn(edgePadding, screenHeight - tooltipHeight - edgePadding)
    )
}

/**
 * Calculate horizontally centered X position with edge detection
 */
private fun calculateCenteredX(
    targetRect: Rect,
    tooltipWidth: Float,
    screenWidth: Float,
    edgePadding: Float
): Float {
    val centeredX = (targetRect.left + targetRect.right) / 2 - tooltipWidth / 2

    return when {
        centeredX < edgePadding -> edgePadding
        centeredX + tooltipWidth > screenWidth - edgePadding -> screenWidth - tooltipWidth - edgePadding
        else -> centeredX
    }
}

/**
 * Calculate vertically centered Y position with edge detection
 */
private fun calculateCenteredY(
    targetRect: Rect,
    tooltipHeight: Float,
    screenHeight: Float,
    edgePadding: Float
): Float {
    val centeredY = (targetRect.top + targetRect.bottom) / 2 - tooltipHeight / 2

    return when {
        centeredY < edgePadding -> edgePadding
        centeredY + tooltipHeight > screenHeight - edgePadding -> screenHeight - tooltipHeight - edgePadding
        else -> centeredY
    }
}

/**
 * Intelligently determine the best tooltip position based on available space
 */
private fun determineBestPosition(
    preferredPosition: TooltipPosition,
    spaceAbove: Float,
    spaceBelow: Float,
    spaceLeft: Float,
    spaceRight: Float
): TooltipPosition {
    // If preferred position has enough space, use it
    val hasSpaceInPreferred = when (preferredPosition) {
        TooltipPosition.Top -> spaceAbove >= 0
        TooltipPosition.Bottom -> spaceBelow >= 0
        TooltipPosition.Left -> spaceLeft >= 0
        TooltipPosition.Right -> spaceRight >= 0
    }

    if (hasSpaceInPreferred) {
        return preferredPosition
    }

    // Otherwise, find the position with most space
    val spaces = mapOf(
        TooltipPosition.Bottom to spaceBelow,
        TooltipPosition.Top to spaceAbove,
        TooltipPosition.Right to spaceRight,
        TooltipPosition.Left to spaceLeft
    )

    // Return position with maximum space
    return spaces.maxByOrNull { it.value }?.key ?: preferredPosition
}
