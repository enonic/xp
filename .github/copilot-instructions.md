# GitHub Copilot Instructions for Enonic XP

## Project Overview

Enonic XP is a Java-based web operating system and application platform. This repository contains the core runtime of the Enonic XP platform.

## Build and Test Commands

### Building the project
- Full build with all tests: `./gradlew build`
- Build without tests: `./gradlew build -x check`
- Build without integration tests: `./gradlew build -x integrationTest`
- CI build: `./gradlew ci --scan`

### Testing
- Run all tests: `./gradlew test`
- Run integration tests: `./gradlew integrationTest`
- Run tests for a specific module: `./gradlew :module-name:test`

### Other commands
- Clean build artifacts: `./gradlew clean`
- Generate documentation: Documentation is generated as part of the build process

## Technology Stack

- **Language**: Java 21 (JDK 21 required for building, GraalVM Java 21 for running)
- **Build Tool**: Gradle with version catalog (libs.versions.toml)
- **Testing**: JUnit 5, Mockito, AssertJ
- **Key Dependencies**: 
  - OSGi for modularity
  - Guava for utilities
  - SLF4J/Logback for logging
  - Jackson for JSON processing
  - Jetty for web server
  - Hazelcast for clustering

## Project Structure

- `modules/` - Main source code organized into logical modules
  - `core/` - Core API and implementation modules
  - `lib/` - JavaScript API libraries for application developers
  - `web/` - Web-related modules (Jetty, servlets, etc.)
  - `portal/` - Portal engine implementation
  - `admin/` - Admin console implementation
  - `server/` - Server configuration and management
  - `script/` - JavaScript engine integration
  - `runtime/` - Runtime assembly
- `buildSrc/` - Custom Gradle build logic
- `gradle/` - Gradle wrapper and version catalog

## Code Style and Conventions

### Java Code Style
- **Indentation**: Use spaces, not tabs
- **Brace Style**: Opening braces on same line
- **Naming Conventions**:
  - Classes: PascalCase
  - Methods and variables: camelCase
  - Constants: UPPER_SNAKE_CASE
  - Package names: lowercase, typically `com.enonic.xp.*`
- **Imports**: No wildcard imports; unused imports are not allowed
- **Modifiers**: Follow standard Java modifier order
- **Checkstyle**: The project uses Checkstyle with configuration in `checkstyle.xml`
  - Enforces ConstantName, ParameterName, MemberName
  - Checks for unused imports
  - Prohibits imports from: `sun`, `org.mockito.internal`, `org.apache.commons.io`, `org.apache.commons.lang`

### Testing Conventions
- Use JUnit 5 for all tests
- Test classes should end with `Test` (e.g., `XmlContentTypeParserTest`)
- Use AssertJ for assertions: `assertThat(value).isEqualTo(expected)`
- Also acceptable: JUnit assertions like `assertEquals()`, `assertNotNull()`, `assertTrue()`, `assertFalse()`
- Use Mockito for mocking
- Test method names should be descriptive (e.g., `testParse()`, `testParse_noNs()`)
- Use `@BeforeEach` for setup, not `@Before`

### OSGi and Modularity
- Use OSGi service component annotations for dependency injection
- Export packages explicitly in `build.gradle` bundle configuration
- API modules should export public API packages only
- Internal packages should not be exported (use `!com.enonic.xp.core.internal.*` pattern)

### Dependency Management
- Use version catalog defined in `gradle/libs.versions.toml`
- Reference dependencies as `libs.groupname.artifactname` in build.gradle
- Keep dependency versions centralized in the version catalog
- Prefer `api` for dependencies that are part of public API
- Use `implementation` for internal dependencies
- Use `compileOnlyApi` for compile-time only dependencies like OSGi annotations

## Public API Guidelines
- Mark public APIs with `@PublicApi` annotation
- Public API exceptions should extend `BaseException`
- Maintain backward compatibility for public APIs
- Public API modules typically end with `-api`

## Documentation
- Use JavaDoc for public APIs
- Documentation is generated during build process
- External documentation available at: https://developer.enonic.com/docs/xp/stable

## Module Organization
- Each module typically has:
  - `src/main/java` - Java source code
  - `src/main/resources` - Resources (config files, etc.)
  - `src/test/java` - Test code
  - `src/test/resources` - Test resources
  - `build.gradle` - Module-specific build configuration

## Best Practices
- Follow existing code patterns and conventions in the repository
- Keep changes minimal and focused
- Ensure all tests pass before submitting changes
- Use the project's existing utilities and base classes
- Respect the modular architecture - don't create circular dependencies
- When adding new dependencies, add them to the version catalog first

## Licensing
- Core platform: GPL v3 with linking exception
- Library modules (`lib-*`): Apache 2.0
- Ensure new code complies with the appropriate license
