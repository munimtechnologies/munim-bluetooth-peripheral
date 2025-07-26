# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Enhanced documentation with comprehensive examples
- Improved README with better structure and API reference
- Updated Code of Conduct with proper contact information

## [0.2.4] - 2025-01-26

### Changed

- Renamed package from `@sheehanmunim/react-native-bluetooth-peripheral` to `munim-bluetooth-peripheral`
- Updated GitHub repository to `munimtechnologies/munim-bluetooth-peripheral`
- Updated all documentation and references to reflect new package name

### Fixed

- Updated package.json with correct repository URLs
- Updated podspec file with new git source URL
- Regenerated lock files with new package name

## [0.2.3] - 2024-12-15

### Added

- Support for React Native 0.79
- Enhanced TypeScript definitions
- Improved error handling for BLE operations

### Fixed

- Memory leaks in iOS implementation
- Android permission handling issues
- Connection state management improvements

## [0.2.2] - 2024-11-20

### Added

- New architecture (Fabric) support
- Better event handling system
- Improved debugging capabilities

### Changed

- Updated to React Native 0.78 compatibility
- Enhanced performance optimizations

## [0.2.1] - 2024-10-10

### Added

- Support for manufacturer data in advertising
- Enhanced characteristic properties handling
- Better iOS background mode support

### Fixed

- Android 14 compatibility issues
- iOS permission handling improvements
- Memory management optimizations

## [0.2.0] - 2024-09-01

### Added

- Full TypeScript support
- Comprehensive API documentation
- Event listener system for connection state changes
- Support for multiple services and characteristics

### Changed

- Complete rewrite with new architecture
- Improved error handling and logging
- Better cross-platform compatibility

### Breaking Changes

- API has been significantly restructured
- Event handling system completely redesigned

## [0.1.10] - 2024-08-15

### Fixed

- Critical bug in service advertising
- iOS connection stability issues
- Android permission handling

## [0.1.9] - 2024-07-20

### Added

- Basic event system
- Improved error messages
- Better debugging support

### Fixed

- Memory leaks in iOS implementation
- Android service discovery issues

## [0.1.8] - 2024-06-10

### Added

- Initial release
- Basic BLE peripheral functionality
- Support for service advertising
- Characteristic read/write operations

---

## Migration Guide

### From 0.1.x to 0.2.0

The 0.2.0 release includes significant breaking changes. Please refer to the updated documentation for migration instructions.

### From 0.2.x to 0.2.4

The package has been renamed from `@sheehanmunim/react-native-bluetooth-peripheral` to `munim-bluetooth-peripheral`. Update your package.json:

```json
{
  "dependencies": {
    "munim-bluetooth-peripheral": "^0.2.4"
  }
}
```

And update your imports:

```js
// Old
import { startAdvertising } from '@sheehanmunim/react-native-bluetooth-peripheral';

// New
import { startAdvertising } from 'munim-bluetooth-peripheral';
```
