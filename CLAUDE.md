# CLAUDE.md

## Project Overview

Kotlin backend project built with the Ktor framework, implementing a Task API with multi-module architecture for clean separation between domain logic, API contracts, and HTTP handling.

## Architecture

The project uses a multi-module architecture with clear dependency flow:

| Module                                                   | Description                                                | Documentation                                             |
|----------------------------------------------------------|------------------------------------------------------------|-----------------------------------------------------------|
| [lucid-be-ktor-app](lucid-be-ktor-app)                   | HTTP server application (Ktor + Netty)                     | [ktor-app.md](docs/modules/ktor-app.md)                   |
| [lucid-be-common](lucid-be-common)                       | Domain models & business logic                             | [common.md](docs/modules/common.md)                       |
| [lucid-be-transport-openapi](lucid-be-transport-openapi) | OpenAPI specs & generated models                           | [transport-openapi.md](docs/modules/transport-openapi.md) |
| [lucid-be-mappers](lucid-be-mappers)                     | Mappers for converting between domain and transport models | [mappers.md](docs/modules/mappers.md)                     |

**Module Dependency Graph:**
```
lucid-be-ktor-app
├── depends on → lucid-be-common
├── depends on → lucid-be-transport-openapi
└── depends on → lucid-be-mappers

lucid-be-mappers
├── depends on → lucid-be-common
└── depends on → lucid-be-transport-openapi

lucid-be-common
└── (no project dependencies)

lucid-be-transport-openapi
└── (no project dependencies)
```

## Quick Reference

### Common Commands

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :module-name:build

# Run application
./gradlew :lucid-be-ktor-app:run

# Run specific module tests
./gradlew :module-name:test

# Clean build
./gradlew clean build
```

### File Locations

See [File Locations Guide](docs/guides/file-locations.md) for comprehensive reference on where files belong.

### Dependencies

See [Dependency Management Guide](docs/guides/dependencies.md) for adding libraries and managing module dependencies.

### Development Standards

See [Conventions Guide](docs/guides/conventions.md) for coding standards, patterns, and best practices.

### Testing

This project uses both Kotlin's built-in test framework and Kotest for different modules. See [Testing Guide](docs/guides/testing.md) for comprehensive information about testing patterns and practices.

## How-To Guides

Complete practical guides in [Development Guide](docs/development-guide.md):

- [Adding a Module](docs/development-guide.md#adding-a-module) - Create new Gradle module with proper structure
- [Adding a Domain Model](docs/development-guide.md#adding-a-domain-model) - Create business entities in common module
- [Adding an API Endpoint](docs/development-guide.md#adding-an-api-endpoint) - Define OpenAPI spec and implement handlers
- [Creating Mappers](docs/development-guide.md#creating-mappers) - Convert between domain and transport models
- [Adding Dependencies](docs/development-guide.md#adding-dependencies) - Use version catalog pattern
- [Testing Patterns](docs/development-guide.md#testing-patterns) - Unit and integration test examples
- [Common Gotchas](docs/development-guide.md#common-gotchas) - Solutions to frequent issues

## Architecture & Design

Understand the rationale behind technical decisions in [Architecture Decision Records](docs/architecture-decisions.md):

- [Multi-Module Architecture](docs/architecture-decisions.md#1-multi-module-architecture) - Why separate modules
- [Dual Serialization Strategy](docs/architecture-decisions.md#2-dual-serialization-strategy) - kotlinx.serialization + Jackson
- [Separate Domain and Transport Models](docs/architecture-decisions.md#3-separate-domain-and-transport-models) - Layer separation
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