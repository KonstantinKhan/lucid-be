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

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :module-name:build

# Clean build
./gradlew clean build

# Run tests
./gradlew test

# Run application
./gradlew :lucid-be-ktor-app:run
```

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

### Dual Serialization Approach

The project intentionally uses two serialization libraries:

#### kotlinx.serialization

**Used in**: `ktor-app` and `common` modules

- **Why**: Ktor standard, Kotlin-native, multiplatform compatible
- **Annotation**: `@Serializable`
- **Use case**: Domain models, internal serialization

#### Jackson

**Used in**: `transport-openapi` module (generated code)

- **Why**: OpenAPI Generator default, industry standard for REST APIs
- **Annotation**: `@JsonProperty` (generated)
- **Use case**: Transport models, API contracts

### Mapper Pattern

Mappers in `ktor-app` module handle conversion:

```kotlin
// Domain -> Transport
fun DomainModel.toTransport(): TransportModel

// Transport -> Domain
fun TransportModel.toDomain(): DomainModel
```

**Location**: `lucid-be-ktor-app/src/main/kotlin/com/khan366kos/mappers/`

See [Dual Serialization Strategy ADR](../architecture-decisions.md#2-dual-serialization-strategy) for full explanation.

## Time Type Handling

### Different Types for Different Layers

The project uses different time types optimized for each layer:

#### Domain Layer (common module)

- **Type**: `kotlinx.datetime.Instant`
- **Why**: Timezone-agnostic (UTC), multiplatform compatible, simpler
- **Use case**: Business logic, storage

#### Transport Layer (transport-openapi module)

- **Type**: `java.time.OffsetDateTime`
- **Why**: OpenAPI Generator default, preserves timezone offset
- **Use case**: API requests/responses

### Conversion Pattern

```kotlin
// Instant -> OffsetDateTime (domain to transport)
private fun Instant.toOffsetDateTime(): OffsetDateTime =
    OffsetDateTime.ofInstant(this.toJavaInstant(), ZoneOffset.UTC)

// OffsetDateTime -> Instant (transport to domain)
private fun OffsetDateTime.toKotlinInstant(): Instant {
    val javaInstant: java.time.Instant = this.toInstant()
    return javaInstant.toKotlinInstant()
}
```

See [Time Type Choices ADR](../architecture-decisions.md#4-time-type-choices) for detailed rationale.

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

### Frameworks & Tools

- **Unit Tests**: Kotlin Test with JUnit
- **Integration Tests**: Ktor test host
- **Assertions**: Kotlin test assertions (`assertEquals`, `assertFailsWith`, etc.)

### Testing Patterns

1. **Domain Model Tests**:
   - Valid object creation
   - Validation rules enforcement
   - Serialization round-trips
   - Data class copy functionality

2. **Mapper Tests**:
   - Domain ↔ Transport conversion accuracy
   - Time type conversion correctness
   - Collection handling (null vs empty)

3. **API Integration Tests**:
   - HTTP status codes
   - Request/response structure
   - Error handling

### Test Structure

```kotlin
class EntityTest {
    @Test
    fun `entity creation with valid data succeeds`() {
        // Arrange, Act, Assert
    }

    @Test
    fun `entity with invalid data fails validation`() {
        assertFailsWith<IllegalArgumentException> {
            // Invalid entity creation
        }
    }
}
```

See [Testing Patterns](../development-guide.md#testing-patterns) for detailed examples.

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

### Strict Dependency Flow

```
lucid-be-ktor-app
├── depends on → lucid-be-common ✓
└── depends on → lucid-be-transport-openapi ✓

lucid-be-common
└── no project dependencies ✓

lucid-be-transport-openapi
└── no project dependencies ✓
```

### Rules

1. **Common module**: NO dependencies on other project modules (pure domain)
2. **Transport module**: NO dependencies on other project modules (pure contracts)
3. **Ktor-app module**: Depends on both common and transport (integration layer)
4. **No circular dependencies**: Dependencies flow in one direction only
5. **Mappers live in ktor-app**: Only ktor-app knows about both domain and transport

### Why These Rules?

- **Dependency Inversion**: Infrastructure depends on domain, not the other way
- **Reusability**: Common and transport modules can be used independently
- **Testability**: Modules can be tested in isolation
- **Maintainability**: Clear boundaries prevent tight coupling

See [Module Dependency Principles ADR](../architecture-decisions.md#5-module-dependency-principles) for complete explanation.

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