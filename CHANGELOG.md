# Changelog

All notable changes to PiscesSpotlight will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.0] - 2025-01-XX

### Fixed
- Fixed tooltip position jumping/flickering between tutorial steps
- Eliminated layout recalculation that caused visible position shifts

### Changed
- Tooltip height now defaults to fixed 200dp (previously dynamic/nullable)
- Tooltip content is now scrollable when it exceeds available space
- Title text limited to 2 lines with ellipsis
- Description text limited to 5 lines with ellipsis
- Button now anchored to bottom of tooltip card using weighted spacer

### Added
- Binary compatibility validator plugin for API tracking
- Improved tooltip layout consistency across all steps

## [0.1.0] - 2025-01-XX

### Added
- Initial release of PiscesSpotlight
- Cross-platform support for Android, iOS, and Desktop
- Type-safe spotlight target system
- Smart tooltip positioning with automatic edge detection
- Support for multiple concurrent tutorials
- Reactive state management using Compose primitives
- Customizable tooltip positions (Top, Bottom, Left, Right)
- Flat API structure using CompositionLocal
- String-based target support as alternative to type-safe targets
- Tutorial progression controls (next, complete, reset)
- Platform-specific screen dimension handling

[0.2.0]: https://github.com/xcodebn/piscespotlight/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/xcodebn/piscespotlight/releases/tag/v0.1.0