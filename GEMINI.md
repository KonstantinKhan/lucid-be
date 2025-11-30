# GEMINI.md

## Project Overview

This is a Kotlin backend project built with the Ktor framework.

The project includes an [OpenAPI specification](#api-specification) for a "Task API".

## Architecture

The project uses a multi-module architecture:

| module                                                   | description                                                        | documentation                                             |
|----------------------------------------------------------|--------------------------------------------------------------------|-----------------------------------------------------------|
| [lucid-be-ktor-app](lucid-be-ktor-app)                   | An application for processing HTTP-requests and working with tasks | [ktor-app.md](docs/modules/ktor-app.md)                   |
| [lucid-be-transport-openapi](lucid-be-transport-openapi) | OpenAPI specification bundling and Kotlin model code generation    | [transport-openapi.md](docs/modules/transport-openapi.md) |

## API Specification

[Full API Specification](./docs/api_description.md)

[API Specification Conventions](./docs/api_convention.md)

## Building and Running

The project is built using Gradle. The Gradle wrapper (`./gradlew`) is included in the repository.

### Building the project

To build the project, run the following command from the root directory:

```bash
./gradlew build
```

### Running the application

To run the application, use the following command:

```bash
./gradlew :lucid-be-ktor-app:run
```

The application will start on a local port (default is 8080).

### Running tests

To run the tests, use the following command:

```bash
./gradlew test
```

## Development Conventions

### Code Style

- Follow standard Kotlin coding conventions (`kotlin.code.style=official`)
- Prefer immutable values (val) over mutable variables (var)
- Use data classes for models and DTOs

### Project Organization

- Multi-module structure with clear separation of concerns
- `lucid-be-ktor-app`: Main HTTP server application
- `lucid-be-transport-openapi`: OpenAPI code generation module
- Shared configuration in root `build.gradle.kts`

### Dependency Management

- Gradle version catalog in `gradle/libs.versions.toml` for centralized dependency versions
- Current versions: Kotlin 2.2.20, Ktor 3.3.2, Exposed 0.61.0, Jackson 2.18.1

### OpenAPI Development

- Maintain OpenAPI specifications in modular format under `specs/` directory
- Specifications are automatically bundled during build using Redocly CLI
- Kotlin models are generated from bundled specification using OpenAPI Generator
- Generated code is in `build/` directory and not version controlled

### Serialization

- Main application uses `kotlinx.serialization` for JSON serialization
- Generated OpenAPI models use Jackson (intentional for OpenAPI Generator compatibility)
- Both serialization libraries coexist in the project

### Database Configuration

- Configuration in `resources/application.conf` file (HOCON format)
- PostgreSQL connection details configurable via environment variables
- Exposed framework for SQL operations
- H2 database available for testing

### Logging

- `logback` for logging with configuration in `logback.xml`
- Default log level: INFO
- Console output with timestamp and thread information

### Testing

- Tests in `src/test/kotlin/` directory
- Use Ktor's `testApplication` for integration tests
- JUnit framework for test execution

### Generated Code

- Generated code from OpenAPI is in `build/generated/openapi/`
- Excluded from version control
- Always regenerate after OpenAPI specification changes
- Do not manually modify generated files
