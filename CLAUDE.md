# CLAUDE.md

## Project overview

This is a Kotlin backend project built with the Ktor framework.

The project includes an [OpenAPI specification](#api-specification) for a "Task API".

## Architecture

The project uses a multi-module architecture

| module                                                   | description                                                        | documentation                                             |
|----------------------------------------------------------|--------------------------------------------------------------------|-----------------------------------------------------------|
| [lucid-be-ktor-app](lucid-be-ktor-app)                   | An application for processing HTTP-requests and working with tasks | [ktor-app.md](docs/modules/ktor-app.md)                   |
| [lucid-be-transport-openapi](lucid-be-transport-openapi) | OpenAPI specification bundling and Kotlin model code generation    | [transport-openapi.md](docs/modules/transport-openapi.md) |

## API Specification

[Full API Specification](./docs/api_description.md)

[API Specification Conventions](./docs/api_convention.md)

## Development Conventions

### Code Style

- Follow Kotlin official coding conventions (`kotlin.code.style=official`)
- Prefer immutable values (val) over mutable variables (var)
- Use data classes for models and DTOs

### Project Structure

- Multi-module architecture with clear separation of concerns
- Each module has single, well-defined responsibility

### Build System

- Gradle with Kotlin DSL
- Version catalog in `gradle/libs.versions.toml` for centralized dependency management

### OpenAPI Workflow

1. Update OpenAPI specifications in `specs/` directory
2. Run `./gradlew :lucid-be-transport-openapi:build` to regenerate models
3. Generated code is in `build/` directory and not version controlled
4. Use generated models in `lucid-be-ktor-app` module

### Serialization

- **ktor-app module:** kotlinx.serialization (Ktor standard)
- **transport-openapi module:** Jackson (OpenAPI Generator standard)
- Both approaches coexist intentionally for tool compatibility

### Database

- Exposed SQL framework for database operations
- PostgreSQL for production
- H2 for testing

### Testing

- JUnit test framework
- Ktor test host for integration tests

### Git Workflow

- Main branch: `main`
- Generated code excluded from version control
