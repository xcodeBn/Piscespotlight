package io.piscesbn.xcodebn.piscespotlight.spotlight

/**
 * Represents a spotlight target that can be highlighted in a tutorial.
 *
 * This interface serves as a marker for UI components that can be targeted
 * by the spotlight tutorial system. Users implement this interface in their
 * application code to define type-safe targets, or use the provided [StringTarget]
 * for dynamic scenarios.
 *
 * Usage:
 * ```kotlin
 * // Type-safe approach (recommended)
 * data object LoginButtonTarget : SpotlightTarget
 * data object SignupFormTarget : SpotlightTarget
 *
 * // String-based approach
 * val target: SpotlightTarget = StringTarget("login_button")
 * ```
 *
 * @see StringTarget
 * @see SpotlightStep
 */
interface SpotlightTarget

/**
 * String-based implementation of [SpotlightTarget].
 *
 * This implementation provides flexibility for dynamic target creation or when
 * type-safe targets are not practical. Primarily used for backward compatibility
 * and runtime target generation.
 *
 * @property key The unique identifier for this target
 */
data class StringTarget(val key: String) : SpotlightTarget

/**
 * Converts a String to a [SpotlightTarget].
 *
 * This extension function provides a convenient way to create string-based targets
 * from string literals.
 *
 * Example:
 * ```kotlin
 * val target = "login_button".toTarget()
 * ```
 *
 * @return A [StringTarget] wrapping this string
 */
fun String.toTarget(): SpotlightTarget = StringTarget(this)
