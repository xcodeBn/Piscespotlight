package io.piscesbn.xcodebn.piscespotlight.spotlight

/**
 * Represents a single step in a spotlight tutorial sequence.
 *
 * Each step defines which UI component to highlight, what content to display in the tooltip,
 * and the preferred tooltip position. The tooltip position is a preference only; the system
 * will automatically adjust the position if there is insufficient space on screen.
 *
 * @property targetKey The target component to highlight. Can be a type-safe data object
 *                     implementing [SpotlightTarget] or a [StringTarget].
 * @property title The title text displayed in the tooltip
 * @property description The description text displayed in the tooltip
 * @property tooltipPosition Preferred position for the tooltip relative to the target.
 *                          Defaults to [TooltipPosition.Bottom]. The system will
 *                          automatically adjust if the preferred position would cause
 *                          the tooltip to render off-screen.
 *
 * @see SpotlightTarget
 * @see TooltipPosition
 * @see PiscesTutorialConfig
 */
data class SpotlightStep(
    val targetKey: SpotlightTarget,
    val title: String,
    val description: String,
    val tooltipPosition: TooltipPosition = TooltipPosition.Bottom
) {
    /**
     * Convenience constructor for string-based targets.
     *
     * This constructor allows creating a [SpotlightStep] with a string key directly,
     * which is automatically wrapped in a [StringTarget].
     *
     * Usage:
     * ```kotlin
     * SpotlightStep("my_target", "Title", "Description")
     * ```
     *
     * @param key The string identifier for the target
     * @param title The title text displayed in the tooltip
     * @param description The description text displayed in the tooltip
     * @param tooltipPosition Preferred tooltip position
     */
    constructor(
        key: String,
        title: String,
        description: String,
        tooltipPosition: TooltipPosition = TooltipPosition.Bottom
    ) : this(StringTarget(key), title, description, tooltipPosition)
}

/**
 * Defines the preferred position of the tutorial tooltip relative to the highlighted target.
 *
 * The position is a preference only. The system implements smart positioning that will
 * automatically choose the best available position if the preferred position would cause
 * the tooltip to render off-screen or if there is insufficient space.
 *
 * The positioning algorithm:
 * 1. Attempts to use the preferred position
 * 2. If insufficient space, evaluates all four positions
 * 3. Selects the position with the most available space
 *
 * @see SpotlightStep
 */
enum class TooltipPosition {
    /**
     * Position the tooltip above the target
     */
    Top,

    /**
     * Position the tooltip below the target
     */
    Bottom,

    /**
     * Position the tooltip to the left of the target
     */
    Left,

    /**
     * Position the tooltip to the right of the target
     */
    Right
}
