# PiscesSpotlight

A cross-platform spotlight tutorial system for Compose Multiplatform applications.

## Overview

PiscesSpotlight enables developers to create interactive, step-by-step tutorials that guide users through application features by highlighting specific UI components and displaying contextual information through tooltips.

## Features

- **Cross-Platform**: Works on Android, iOS, Desktop, and Web
- **Type-Safe Targets**: Use data objects or strings to identify UI components
- **Smart Positioning**: Automatic tooltip positioning with edge detection
- **Multiple Tutorials**: Support for registering and managing multiple tutorial sequences
- **State Management**: Built on Compose state primitives for reactive updates
- **Flexible API**: Flat API structure using CompositionLocal
- **Customizable**: Control tooltip positions, tutorial flow, and step content

## Installation

### Gradle Version Catalog

Add to your `libs.versions.toml`:

```toml
[versions]
piscesSpotlight = "0.1.0"

[libraries]
piscesSpotlight = { module = "io.github.xcodebn:pisces-spotlight", version.ref = "piscesSpotlight" }
```

Add to your module's `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.piscesSpotlight)
        }
    }
}
```

### Direct Dependency

```kotlin
commonMain.dependencies {
    implementation("io.github.xcodebn:pisces-spotlight:0.1.0")
}
```

## Quick Start

### 1. Define Spotlight Targets

Create type-safe targets for UI components you want to highlight:

```kotlin
import io.piscesbn.xcodebn.piscespotlight.spotlight.SpotlightTarget

data object LoginButton : SpotlightTarget
data object SignupForm : SpotlightTarget
data object SettingsIcon : SpotlightTarget
```

### 2. Configure Tutorial Steps

Define the sequence of steps for your tutorial:

```kotlin
import io.piscesbn.xcodebn.piscespotlight.spotlight.*

val onboardingSteps = listOf(
    SpotlightStep(
        targetKey = LoginButton,
        title = "Login",
        description = "Tap here to access your account",
        tooltipPosition = TooltipPosition.Bottom
    ),
    SpotlightStep(
        targetKey = SignupForm,
        title = "Create Account",
        description = "New users can sign up here",
        tooltipPosition = TooltipPosition.Top
    ),
    SpotlightStep(
        targetKey = SettingsIcon,
        title = "Settings",
        description = "Customize your preferences",
        tooltipPosition = TooltipPosition.Left
    )
)
```

### 3. Wrap Your Application

Wrap your application content with `PiscesSpotlightContainer`:

```kotlin
import io.piscesbn.xcodebn.piscespotlight.spotlight.*

@Composable
fun App() {
    MaterialTheme {
        PiscesSpotlightContainer(
            tutorials = listOf(
                PiscesTutorialConfig(
                    id = "onboarding",
                    steps = onboardingSteps
                )
            ),
            onTutorialComplete = { tutorialId ->
                println("Tutorial $tutorialId completed")
                // Save completion state to preferences
            }
        ) {
            MainScreen()
        }
    }
}
```

### 4. Mark Target Components

Apply the `piscesSpotlightTarget` modifier to components:

```kotlin
@Composable
fun MainScreen() {
    Column {
        Button(
            onClick = { /* login logic */ },
            modifier = Modifier.piscesSpotlightTarget(LoginButton)
        ) {
            Text("Login")
        }

        Card(
            modifier = Modifier.piscesSpotlightTarget(SignupForm)
        ) {
            SignupFormContent()
        }

        IconButton(
            onClick = { /* settings */ },
            modifier = Modifier.piscesSpotlightTarget(SettingsIcon)
        ) {
            Icon(Icons.Default.Settings, "Settings")
        }
    }
}
```

## Advanced Usage

### Manual Tutorial Control

Control tutorials programmatically:

```kotlin
@Composable
fun MyApp() {
    val tutorialState = rememberPiscesTutorialState()
    val tutorialController = rememberPiscesTutorialController()
    val scope = rememberCoroutineScope()

    PiscesSpotlightContainer(
        state = tutorialState,
        tutorials = tutorials,
        autoStart = false, // Disable auto-start
        onTutorialComplete = { /* ... */ }
    ) {
        Column {
            Button(
                onClick = {
                    scope.launch {
                        tutorialController.startTutorial("onboarding")
                    }
                }
            ) {
                Text("Start Tutorial")
            }

            Button(
                onClick = {
                    tutorialController.stopTutorial()
                }
            ) {
                Text("Stop Tutorial")
            }

            MainContent()
        }
    }
}
```

### Conditional Tutorials

Show tutorials based on conditions:

```kotlin
val tutorials = listOf(
    PiscesTutorialConfig(
        id = "first_time_user",
        steps = firstTimeSteps,
        enabled = !userHasCompletedOnboarding,
        screenPredicate = { currentScreen == Screen.Home }
    ),
    PiscesTutorialConfig(
        id = "advanced_features",
        steps = advancedSteps,
        enabled = user.isExperienced,
        screenPredicate = { currentScreen == Screen.Advanced }
    )
)
```

### String-Based Targets

For dynamic scenarios, use string-based targets:

```kotlin
val steps = listOf(
    SpotlightStep(
        key = "dynamic_button_1", // String instead of data object
        title = "Dynamic Feature",
        description = "This is a dynamically created feature"
    )
)

// In UI
Button(
    onClick = { },
    modifier = Modifier.piscesSpotlightTarget("dynamic_button_1")
) {
    Text("Dynamic Button")
}
```

## API Reference

### Core Components

#### `PiscesSpotlightContainer`

Main composable that wraps your application and manages tutorial state.

**Parameters:**
- `state`: Tutorial state manager (default: `rememberPiscesTutorialState()`)
- `tutorials`: List of tutorial configurations
- `onTutorialComplete`: Callback invoked when a tutorial completes
- `autoStart`: Whether to automatically start the first enabled tutorial (default: true)
- `content`: Your application content

#### `SpotlightStep`

Defines a single step in a tutorial sequence.

**Properties:**
- `targetKey`: Target component identifier
- `title`: Tooltip title text
- `description`: Tooltip description text
- `tooltipPosition`: Preferred tooltip position (default: `Bottom`)

#### `PiscesTutorialConfig`

Configuration for a tutorial sequence.

**Properties:**
- `id`: Unique tutorial identifier
- `steps`: List of tutorial steps
- `enabled`: Whether the tutorial is enabled (default: true)
- `screenPredicate`: Condition for showing the tutorial (default: always true)

#### `TooltipPosition`

Enum defining tooltip positions:
- `Top`: Position above the target
- `Bottom`: Position below the target
- `Left`: Position to the left of the target
- `Right`: Position to the right of the target

Note: The system automatically adjusts positions if insufficient space is available.

### Modifiers

#### `Modifier.piscesSpotlightTarget(target: SpotlightTarget)`

Marks a composable as a spotlight target.

#### `Modifier.piscesSpotlightTarget(key: String)`

String-based variant of the target modifier.

### State Management

#### `rememberPiscesTutorialState()`

Creates and remembers a tutorial state instance.

#### `rememberPiscesTutorialController()`

Creates a controller for programmatic tutorial management.

**Methods:**
- `suspend fun startTutorial(tutorialId: String)`
- `fun stopTutorial()`
- `fun getTargetPositions(): Map<SpotlightTarget, Rect>`

## Platform Considerations

### Android

Fully supported. No special configuration required.

### iOS

Fully supported. Ensure proper framework configuration in your Kotlin Multiplatform setup.

### Desktop (JVM)

Fully supported. Works with Compose for Desktop applications.

### Web (Wasm/JS)

Fully supported. Compatible with Compose for Web targets.

## Best Practices

### Tutorial Design

1. **Keep Steps Concise**: Limit tutorials to 3-7 steps for optimal user engagement
2. **Clear Descriptions**: Use concise, actionable language in tooltips
3. **Logical Order**: Arrange steps in a natural workflow sequence
4. **Test Positioning**: Verify tooltip positioning on different screen sizes

### Performance

1. **Lazy Registration**: Register tutorials only when needed
2. **Conditional Loading**: Use `screenPredicate` to limit tutorial scope
3. **State Persistence**: Save tutorial completion state to avoid repetition
4. **Memory Management**: Use `resetAll()` if dynamically creating many tutorials

### Accessibility

1. **Alternative Paths**: Ensure features are discoverable without tutorials
2. **Skip Option**: Implement a skip mechanism for experienced users
3. **Contrast**: Ensure tooltip text has sufficient contrast
4. **Text Size**: Keep descriptions readable on small screens

## Troubleshooting

### Tutorial Not Starting

- Verify tutorial is registered before attempting to start
- Check that `enabled` property is true
- Ensure `screenPredicate` returns true for current state
- Confirm target components are composed and positioned

### Tooltip Positioning Issues

- Increase margin/padding around target components
- Verify screen has sufficient space for tooltip
- Check that target component dimensions are measured correctly
- Use different `TooltipPosition` values for constrained layouts

### Target Not Highlighting

- Ensure `piscesSpotlightTarget` modifier is applied
- Verify target key matches between step and modifier
- Check that target component is visible and laid out
- Confirm component is not removed from composition

## License

```
Copyright 2025 Hassan Bazzoun

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Support

For issues, feature requests, or contributions, please visit the [GitHub repository](https://github.com/xcodebn/piscespotlight).
