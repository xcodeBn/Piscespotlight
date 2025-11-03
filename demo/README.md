# PiscesSpotlight Demo Application

A demonstration application showcasing the PiscesSpotlight library in a real-world scenario.

## Overview

This demo simulates a PDF Summarizer application with a complete onboarding tutorial that guides users through the main features. The application demonstrates how to integrate PiscesSpotlight into a production-ready Compose Multiplatform application.

## Features Demonstrated

The demo showcases a 7-step tutorial covering:

1. **Upload PDF** - Document upload functionality
2. **Summarize** - AI-powered summarization
3. **Highlight Tool** - Text highlighting capabilities
4. **Annotate Tool** - Adding notes and comments
5. **Save** - Saving work and annotations
6. **AI Assistant** - Interactive AI chat
7. **Share** - Sharing summaries with others

## Integration Details

This demo uses the published Maven Central version of PiscesSpotlight:

```kotlin
implementation("io.github.xcodebn:pisces-spotlight:0.1.0")
```

## Key Implementation Highlights

### Tutorial Configuration

The demo shows a complete onboarding flow with multiple steps, proper tooltip positioning, and smooth transitions between tutorial steps.

### Settings Menu Integration

A dropdown menu from the settings button allows users to:
- Restart the tutorial at any time
- Access app information

### Target Markers

All interactive UI components are properly marked with `piscesSpotlightTarget` modifiers, demonstrating the type-safe target system.

## Running the Demo

### Android

```bash
./gradlew :demo:installDebug
```

### iOS

```bash
./gradlew :demo:iosSimulatorArm64Test
```

## Code Structure

- **App.kt** - Main application with tutorial configuration
- **DemoTargets.kt** - Type-safe spotlight target definitions

## Usage as Reference

Developers can reference this demo to understand:
- How to structure tutorial configurations
- Best practices for tooltip positioning
- Integration patterns for real applications
- Restart and control mechanisms

## Note

This is a demonstration application. For comprehensive testing and edge case scenarios, refer to the testing module. Which is private and you can't see so make your own if you need one :)
