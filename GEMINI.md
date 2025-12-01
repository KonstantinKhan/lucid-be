# GEMINI.md

## Project Overview

Kotlin backend project built with the Ktor framework, implementing a Task API with multi-module architecture for clean
separation between domain logic, API contracts, HTTP handling, and model mapping.

## Architecture

### Modules

The project uses a multi-module architecture with clear dependency flow. The ktor-app module depends on common, transport-openapi, and mappers. The mappers module depends on both common and transport-openapi to handle conversions between domain and transport models. The common and transport-openapi modules have no project dependencies.

| Module                                                   | Description                                                | Documentation                                             |
|----------------------------------------------------------|------------------------------------------------------------|-----------------------------------------------------------|
| [lucid-be-ktor-app](lucid-be-ktor-app)                   | HTTP server application (Ktor + Netty)                     | [ktor-app.md](docs/modules/ktor-app.md)                   |
| [lucid-be-common](lucid-be-common)                       | Domain models & business logic                             | [common.md](docs/modules/common.md)                       |
| [lucid-be-transport-openapi](lucid-be-transport-openapi) | OpenAPI specs & generated models                           | [transport-openapi.md](docs/modules/transport-openapi.md) |
| [lucid-be-mappers](lucid-be-mappers)                     | Mappers for converting between domain and transport models | [mappers.md](docs/modules/mappers.md)                     |

Modules are registered in [settings.gradle.kts](/settings.gradle.kts).

Common dependencies, subproject configuration, and common tasks are configured in [build.gradle.kts](/build.gradle.kts)

### Dependencies

All the dependencies used are defined in [libs.versions.toml](/gradle/libs.versions.toml)

Module dependencies are defined in the `build.gradle.kts`of the corresponding module

| Module                                                   | Build File                                                       |
|----------------------------------------------------------|------------------------------------------------------------------|
| [lucid-be-ktor-app](lucid-be-ktor-app)                   | [build.gradle.kts](/lucid-be-ktor-app/build.gradle.kts)          |
| [lucid-be-common](lucid-be-common)                       | [build.gradle.kts](/lucid-be-common/build.gradle.kts)            |
| [lucid-be-transport-openapi](lucid-be-transport-openapi) | [build.gradle.kts](/lucid-be-transport-openapi/build.gradle.kts) |
| [lucid-be-mappers](lucid-be-mappers)                     | [build.gradle.kts](/lucid-be-mappers/build.gradle.kts)           |

See [Module Dependency Principles ADR](docs/architecture-decisions.md#5-module-dependency-principles) for complete rules
and rationale.

## Quick Reference

### Common Commands

**Single source of truth** for all Gradle commands:

```bash
# Build all modules
./gradlew build

# Build specific module (replace :module-name: with actual module)
./gradlew :module-name:build

# Run application
./gradlew :lucid-be-ktor-app:run

# Run tests for all modules
./gradlew test

# Run tests for specific module
./gradlew :module-name:test

# Regenerate OpenAPI transport models
./gradlew :lucid-be-transport-openapi:build

# Clean build
./gradlew clean build
```

**Module-specific commands:**

- `:lucid-be-ktor-app:` - HTTP server application
- `:lucid-be-common:` - Domain models
- `:lucid-be-transport-openapi:` - OpenAPI models generation
- `:lucid-be-mappers:` - Mapper utilities

### File Locations

See [File Locations Guide](docs/guides/file-locations.md) for comprehensive reference on where files belong.

### Dependencies

See [Dependency Management Guide](docs/guides/dependencies.md) for adding libraries and managing module dependencies.

### Development Standards

See [Conventions Guide](docs/guides/conventions.md) for coding standards, patterns, and best practices.

### Testing

This project uses both Kotlin's built-in test framework and Kotest for different modules.
See [Testing Guide](docs/guides/testing.md) for comprehensive information about testing patterns and practices.

## How-To Guides

Complete practical guides in [Development Guide](docs/development-guide.md):

- [Adding a Module](docs/development-guide.md#adding-a-module) - Create new Gradle module with proper structure
- [Adding a Domain Model](docs/development-guide.md#adding-a-domain-model) - Create business entities in common module
- [Adding an API Endpoint](docs/development-guide.md#adding-an-api-endpoint) - Define OpenAPI spec and implement
  handlers
- [Creating Mappers](docs/development-guide.md#creating-mappers) - Convert between domain and transport models
- [Adding Dependencies](docs/development-guide.md#adding-dependencies) - Use version catalog pattern
- [Testing Patterns](docs/development-guide.md#testing-patterns) - Unit and integration test examples
- [Common Gotchas](docs/development-guide.md#common-gotchas) - Solutions to frequent issues

## Architecture & Design

Understand the rationale behind technical decisions in [Architecture Decision Records](docs/architecture-decisions.md):

- [Multi-Module Architecture](docs/architecture-decisions.md#1-multi-module-architecture) - Why separate modules
- [Dual Serialization Strategy](docs/architecture-decisions.md#2-dual-serialization-strategy) - kotlinx.serialization +
  Jackson
- [Separate Domain and Transport Models](docs/architecture-decisions.md#3-separate-domain-and-transport-models) - Layer
  separation
- [Time Type Choices](docs/architecture-decisions.md#4-time-type-choices) - Instant vs OffsetDateTime
- [Module Dependency Principles](docs/architecture-decisions.md#5-module-dependency-principles) - Dependency rules
- [OpenAPI-First API Design](docs/architecture-decisions.md#6-openapi-first-api-design) - Spec-first approach

## API Documentation

- [API Specification](docs/api_description.md) - OpenAPI spec structure and organization
- [API Conventions](docs/api_convention.md) - Modular spec composition principles

## Module Documentation

Detailed module-specific information:

| Module                                                 | Purpose                                                    | Quick Reference              |
|--------------------------------------------------------|------------------------------------------------------------|------------------------------|
| [ktor-app](docs/modules/ktor-app.md)                   | HTTP server (Ktor 3.3.2, Netty engine)                     | Build, run, test commands    |
| [common](docs/modules/common.md)                       | Domain models (Task, TaskStatus)                           | Domain patterns, validation  |
| [transport-openapi](docs/modules/transport-openapi.md) | Generated API models                                       | Build pipeline, regeneration |
| [mappers](docs/modules/mappers.md)                     | Mappers for converting between domain and transport models | Mapping patterns, utilities  |

## Additional Resources

**Guides:**

- [Development Guide](docs/development-guide.md) - Practical how-to guides for common tasks
- [File Locations Guide](docs/guides/file-locations.md) - Where different file types belong
- [Dependencies Guide](docs/guides/dependencies.md) - Managing project dependencies
- [Conventions Guide](docs/guides/conventions.md) - Coding standards and patterns

**Reference:**

- [Architecture Decisions](docs/architecture-decisions.md) - ADRs explaining design choices
- [Module Documentation](docs/modules/) - Detailed per-module information
- [API Documentation](docs/api_description.md) - OpenAPI specification details
