# GEMINI.md

## Project Overview

This is a Kotlin backend project built with the Ktor framework.

The project includes an [OpenAPI specification](#api-specification) for a "Task API".

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

- The project follows the standard Kotlin coding conventions.
- The project is structured into modules, with the main application logic in the `lucid-be-ktor-app` module.
- Configuration is done in the `resources/application.conf` file.
- The project uses `kotlinx.serialization` for JSON serialization.
- The project uses `logback` for logging.
