# Development Conventions Guide

[← Back to CLAUDE.md](../../CLAUDE.md)

Development standards and best practices for the lucid-be project.

## Code Style

### Kotlin Conventions

- Follow Kotlin official coding conventions (`kotlin.code.style=official`)
- Prefer immutable values (`val`) over mutable variables (`var`)
- Use data classes for models and DTOs
- Use expression bodies for simple functions
- Use trailing commas in multi-line declarations

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `Task`, `TaskStatus` |
| Functions | camelCase | `toTransport`, `toDomain`, `createTask` |
| Properties | camelCase | `authorId`, `assigneeIds` |
| Constants | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT`, `DEFAULT_PORT` |
| Packages | lowercase | `com.khan366kos.common.model` |
| Files | Match class name | `Task.kt`, `TaskStatus.kt` |

### Code Organization

- One class/enum per file (unless closely related sealed classes)
- File name matches primary class name
- Organize members: companion object, properties, init, methods
- Group related methods together
- Use visibility modifiers explicitly

## Project Structure

### Multi-Module Architecture

The project uses a multi-module architecture with clear separation of concerns:

```
lucid-be-ktor-app       - Application layer (HTTP server)
lucid-be-common         - Domain layer (business logic)
lucid-be-transport-openapi  - Transport layer (API contracts)
lucid-be-mappers        - Mapping layer (conversion between domain and transport models)
```

### Design Principles

1. **Single Responsibility**: Each module has one well-defined purpose
2. **Dependency Inversion**: Domain doesn't depend on infrastructure
3. **Separation of Concerns**: Clear boundaries between layers
4. **Explicit Dependencies**: No circular dependencies

See [Multi-Module Architecture ADR](../architecture-decisions.md#1-multi-module-architecture) for full rationale.

## Build System

### Gradle with Kotlin DSL

- All build files use Kotlin DSL (`.gradle.kts`)
- Version catalog in `gradle/libs.versions.toml` for dependency management
- Consistent build configuration across modules
- Modular plugin application

### Build Configuration Pattern

```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization) // if needed
}

dependencies {
    // Use version catalog references
    implementation(libs.library.name)
    testImplementation(libs.kotlin.test.junit)
}
```

### Common Build Commands

See [Common Commands](../../CLAUDE.md#common-commands) in CLAUDE.md for all Gradle commands.

## OpenAPI Workflow

### Spec-First Development

The project follows an OpenAPI-first approach:

1. **Define API** in OpenAPI YAML specs (modular in `specs/` directory)
2. **Bundle specs** with @redocly/cli into single file
3. **Generate models** with OpenAPI Generator
4. **Implement handlers** using generated models

### Key Principles

- **Contract-First**: API defined independently of implementation
- **Modular Specs**: Split into reusable components with `$ref`
- **Never Edit Generated Code**: Changes go in specs, then regenerate
- **Type Safety**: Generated models ensure implementation matches spec

### Regenerating Models

After updating OpenAPI specs:

```bash
./gradlew :lucid-be-transport-openapi:build
```

See [OpenAPI-First API Design ADR](../architecture-decisions.md#6-openapi-first-api-design) for detailed rationale.

## Serialization Strategy

The project uses two serialization libraries: **kotlinx.serialization** for domain models (`common`, `ktor-app`) and **Jackson** for transport models (`transport-openapi`). Mappers handle conversion between layers. See [Dual Serialization Strategy ADR](../architecture-decisions.md#2-dual-serialization-strategy) for complete rationale and [mappers module](../modules/mappers.md) for implementation details.

## Time Type Handling

Domain models use `kotlinx.datetime.Instant` (UTC, multiplatform), transport models use `java.time.OffsetDateTime` (OpenAPI standard). Mappers handle conversion. See [Time Type Choices ADR](../architecture-decisions.md#4-time-type-choices) for rationale and [mappers module](../modules/mappers.md) for conversion patterns.

## Database

### Framework & Drivers

- **ORM**: Exposed SQL framework (DSL and DAO)
- **Production**: PostgreSQL
- **Testing**: H2 (in-memory)

### Configuration

- Connection details in `application.conf`
- Use Exposed DSL for type-safe queries
- Transactions managed by Exposed
- Future: Database migrations

### Example Pattern

```kotlin
transaction {
    Tasks.selectAll()
        .where { Tasks.authorId eq userId }
        .map { it.toTask() }
}
```

## Testing

The project uses Kotlin Test (JUnit) for unit tests and Ktor test host for integration tests. Test levels: domain model tests, mapper tests, API integration tests. See [Testing Guide](./testing.md) for comprehensive patterns, frameworks, and examples.

## Git Workflow

### Branching Strategy

- **Main branch**: `main` (stable, deployable)
- **Feature branches**: `feature/description`
- **Bugfix branches**: `bugfix/description`

### What's Version Controlled

**Included** ✓:
- Source code
- Tests
- OpenAPI specs (YAML source files)
- Build configuration
- Documentation

**Excluded** ✗:
- Generated code (`build/` directories)
- Bundled OpenAPI spec (`specs/bundled-openapi.yaml`)
- IDE files (`.idea/`, `*.iml`)
- Build caches (`.gradle/`)

### Commit Conventions

- Use clear, descriptive commit messages
- Reference issues when applicable
- Keep commits focused and atomic

## Module Dependency Rules

Follow strict dependency flow: `ktor-app` depends on `common`, `transport-openapi`, and `mappers`; `mappers` depends on `common` and `transport-openapi`; `common` and `transport-openapi` have no project dependencies. No circular dependencies allowed. See [Module Dependency Principles ADR](../architecture-decisions.md#5-module-dependency-principles) for complete rules, rationale, and examples.

## Domain Model Patterns

### Immutable Data Classes

```kotlin
@Serializable
data class Entity(
    val id: String,
    val name: String,
    val createdAt: Instant
)
```

### Validation in Init Blocks

```kotlin
@Serializable
data class Task(
    val id: String,
    val title: String
) {
    init {
        require(title.isNotBlank()) { "Task title cannot be blank" }
    }
}
```

### Default Values for Optional Fields

```kotlin
@Serializable
data class Task(
    val id: String,
    val assigneeIds: List<String> = emptyList(),  // Default to empty, not null
    val priority: Int? = null  // Truly optional
)
```

## API Patterns

### RESTful Conventions

- Use proper HTTP verbs (GET, POST, PUT, DELETE)
- Use plural resource names (`/tasks`, not `/task`)
- Use HTTP status codes correctly (200, 201, 400, 404, 500)
- Return consistent error format

### Routing Organization

```kotlin
fun Application.configureRouting() {
    routing {
        route("/api/v1") {
            route("/tasks") {
                get { /* list tasks */ }
                post { /* create task */ }
                route("/{id}") {
                    get { /* get task */ }
                    put { /* update task */ }
                    delete { /* delete task */ }
                }
            }
        }
    }
}
```

## See Also

- [CLAUDE.md](../../CLAUDE.md) - Project navigation hub
- [Architecture Decision Records](../architecture-decisions.md) - Why these decisions were made
- [Development Guide](../development-guide.md) - How to implement these conventions
- [File Locations Guide](./file-locations.md) - Where files belong
- [Dependencies Guide](./dependencies.md) - Managing dependencies