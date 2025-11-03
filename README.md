# PiscesSpotlight

A cross-platform spotlight tutorial system for Compose Multiplatform.

## About

PiscesSpotlight is a Kotlin Multiplatform library that provides an elegant solution for implementing interactive user tutorials in Compose applications. The library enables developers to create step-by-step guides that highlight specific UI components and display contextual information through positioned tooltips.

## Key Features

- Cross-platform support for Android, iOS, Desktop, and Web
- Type-safe target system with compile-time verification
- Smart tooltip positioning with automatic edge detection
- Reactive state management using Compose primitives
- Support for multiple concurrent tutorial configurations
- Clean, flat API structure without nested dependencies

## Installation

Add PiscesSpotlight to your Compose Multiplatform project:

```kotlin
commonMain.dependencies {
    implementation("io.github.xcodebn:pisces-spotlight:0.1.0")
}
```

For detailed installation instructions, see the [library documentation](./spotlight/README.md).

## Quick Example

```kotlin
import io.piscesbn.xcodebn.piscespotlight.spotlight.*

// Define targets
data object LoginButton : SpotlightTarget

// Configure tutorial
@Composable
fun App() {
    PiscesSpotlightContainer(
        tutorials = listOf(
            PiscesTutorialConfig(
                id = "onboarding",
                steps = listOf(
                    SpotlightStep(
                        LoginButton,
                        "Login",
                        "Tap here to access your account"
                    )
                )
            )
        ),
        onTutorialComplete = { id ->
            println("Tutorial $id completed")
        }
    ) {
        Button(
            onClick = { },
            modifier = Modifier.piscesSpotlightTarget(LoginButton)
        ) {
            Text("Login")
        }
    }
}
```

## Project Structure

```
piscespotlight/
├── spotlight/              # Library module
│   ├── src/
│   │   └── commonMain/
│   │       └── kotlin/
│   │           └── io/piscesbn/xcodebn/piscespotlight/
│   │               ├── PiscesSpotlight.kt
│   │               ├── SpotlightStep.kt
│   │               └── SpotlightTarget.kt
│   ├── build.gradle.kts
│   └── README.md          # Comprehensive library documentation
├── demo/                  # Demo application
│   └── src/
│       └── commonMain/
│           └── kotlin/
│               └── io/piscesbn/xcodebn/piscespotlight/
│                   ├── App.kt
│                   └── DemoTargets.kt
└── build.gradle.kts
```

## Documentation

- **Library Documentation**: Comprehensive API reference and usage guide in [`spotlight/README.md`](./spotlight/README.md)
- **Publishing Guide**: Maven Central publishing instructions in [`PUBLISHING.md`](./PUBLISHING.md)
- **Demo Documentation**: Demo application guide in [`demo/README.md`](./demo/README.md)

## Platform Support

| Platform | Status | Notes |
|----------|--------|-------|
| Android | Supported | API 24+ |
| iOS | Supported | arm64, simulator arm64 |
| Desktop (JVM) | Supported | Compose for Desktop |
| Web | Planned | Future release |

## Building

### Prerequisites

- JDK 11 or higher
- Android Studio or IntelliJ IDEA
- Kotlin 2.2.20 or higher

### Build Commands

```bash
# Build all targets
./gradlew build

# Build library only
./gradlew :spotlight:build

# Build demo application
./gradlew :demo:build

# Publish to Maven Local
./gradlew :spotlight:publishToMavenLocal

# Run tests
./gradlew test
```

## Demo Application

The `demo` module contains a fully functional demo showcasing the library's capabilities. The demo simulates a PDF summarizer application with a comprehensive 7-step onboarding tutorial demonstrating real-world integration patterns.

To run the demo:

```bash
# Android
./gradlew :demo:installDebug

# iOS (macOS only)
./gradlew :demo:iosSimulatorArm64Test
```

See [`demo/README.md`](./demo/README.md) for detailed demo documentation.

## Contributing

Contributions are welcome. Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Create a Pull Request

### Development Guidelines

- Follow Kotlin coding conventions
- Add KDoc comments for public APIs
- Include unit tests for new features
- Update documentation as needed
- Verify cross-platform compatibility

## Versioning

This project uses [Semantic Versioning](https://semver.org/). Version history:

- **0.1.0** (Current) - Initial release
  - Core spotlight functionality
  - Type-safe and string-based targets
  - Smart tooltip positioning
  - Multiple tutorial support
  - State management improvements

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

See the [LICENSE](./LICENSE) file for details.

## Author

Hassan Bazzoun
- Email: hassan.bazzoundev@gmail.com
- GitHub: [@xcodebn](https://github.com/xcodebn)

## Acknowledgments

- Built with [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- Powered by [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- Published to [Maven Central](https://central.sonatype.com/)

## Support

For bug reports and feature requests, please use the [GitHub issue tracker](https://github.com/xcodebn/piscespotlight/issues).

For questions and discussions, please use [GitHub Discussions](https://github.com/xcodebn/piscespotlight/discussions).
