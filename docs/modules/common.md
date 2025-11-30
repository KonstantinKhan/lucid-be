# lucid-be-common

[← Back to CLAUDE.md](../../CLAUDE.md)

## Quick Reference

| Property | Value |
|----------|-------|
| **Purpose** | Domain models with business validation |
| **Dependencies** | kotlinx-serialization (1.6.0), kotlinx-datetime (0.6.0) |
| **Package** | `com.khan366kos.common.model` |
| **Used by** | lucid-be-ktor-app |
| **Build** | `./gradlew :lucid-be-common:build` |
| **Test** | `./gradlew :lucid-be-common:test` |
| **Models** | Task, TaskStatus |

**Key Characteristics:**
- Immutable data classes (`val` properties)
- Domain validation in `init` blocks
- kotlinx.serialization (`@Serializable`)
- kotlinx.datetime.Instant for timestamps

---

## Overview

Core domain module containing pure business entities and domain models. This module is independent of any framework or transport layer, representing the heart of the business logic.

## Purpose

Separates domain models from API transport concerns:
- **Domain Models** (this module): Business entities with validation and domain rules
- **Transport Models** (transport-openapi): API contracts with Jackson serialization

This separation enables:
- Clean architecture and dependency inversion
- Reusable models across different interfaces (REST, CLI, batch jobs)
- Domain-driven design principles
- Independent evolution of API and domain

## Architecture

**Package Structure:**
- `com.khan366kos.common.model` - Domain entities (Task, TaskStatus)
- Future: `com.khan366kos.common.validation`, `com.khan366kos.common.error`

**Design Principles:**
- Immutable data classes
- Domain validation in `init` blocks
- Kotlin-native types (Instant, not OffsetDateTime)
- kotlinx.serialization for JSON support

## Domain Models

### Task

Represents a task in the system with full lifecycle tracking.

**Location:** `com.khan366kos.common.model.Task`

**Properties:**
- `id: String` - Unique identifier
- `title: String` - Required, non-blank title
- `description: String?` - Optional detailed description
- `createdAt: Instant` - Creation timestamp
- `updatedAt: Instant` - Last modification timestamp
- `status: TaskStatus` - Current lifecycle status
- `authorId: String` - User who created the task
- `assigneeIds: List<String>` - Users assigned to the task (default: empty list)
- `priority: Int?` - Optional priority (must be positive)
- `plannedTime: Int?` - Planned duration in seconds
- `actualTime: Int?` - Actual time spent in seconds

**Validation Rules:**
- Title cannot be blank
- Priority must be positive if specified
- Time values cannot be negative

### TaskStatus

Enum representing task lifecycle.

**Location:** `com.khan366kos.common.model.TaskStatus`

**Values:**
- `NEW` - Newly created task
- `IN_PROGRESS` - Work has started
- `DONE` - Task completed
- `CANCELED` - Task abandoned

## Dependencies

- **kotlinx.serialization-json** (1.6.0) - JSON serialization support
- **kotlinx-datetime** (0.6.0) - Multiplatform date/time handling

## Usage

### Creating Tasks

```kotlin
import com.khan366kos.common.model.Task
import com.khan366kos.common.model.TaskStatus
import kotlinx.datetime.Clock

val task = Task(
    id = "task-123",
    title = "Implement feature",
    description = "Add user authentication",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    status = TaskStatus.NEW,
    authorId = "user-456",
    assigneeIds = listOf("user-789"),
    priority = 1
)
```

### Updating Tasks

```kotlin
val updated = task.copy(
    status = TaskStatus.IN_PROGRESS,
    updatedAt = Clock.System.now()
)
```

## Integration with Other Modules

### Used By

- **lucid-be-ktor-app**: HTTP application uses domain models internally

### Mapping to Transport Models

The ktor-app module contains mappers to convert between domain and transport models:

**Location:** `com.khan366kos.mappers.TaskMappers`

```kotlin
import com.khan366kos.mappers.toTransport
import com.khan366kos.mappers.toDomain

// Domain -> Transport (for API responses)
val transportTask = domainTask.toTransport()

// Transport -> Domain (from API requests)
val domainTask = transportTask.toDomain()
```

**Key Differences:**
- **Time Types**: Domain uses `kotlinx.datetime.Instant`, Transport uses `java.time.OffsetDateTime`
- **Serialization**: Domain uses kotlinx.serialization, Transport uses Jackson
- **Collections**: Domain uses non-null lists with defaults, Transport uses nullable lists
- **Enum Values**: Domain uses uppercase (NEW), Transport uses lowercase/snake_case (new)

## Build

```bash
# Build common module
./gradlew :lucid-be-common:build

# Run tests
./gradlew :lucid-be-common:test

# Build all modules
./gradlew build
```

## Testing

Tests are located in `src/test/kotlin/com/khan366kos/common/model/TaskTest.kt`

Test coverage includes:
- Valid object creation
- Domain validation rules (blank title, negative priority/time)
- Serialization round-trip
- Data class copy functionality
- All status values
- Optional fields handling

Run tests:
```bash
./gradlew :lucid-be-common:test
```

## Future Enhancements

Potential additions to this module:
- **User domain model** - User entities with roles and permissions
- **Common validation** - Shared validation utilities
- **Domain events** - Event classes for domain-driven design
- **Value objects** - Email, PhoneNumber, etc.
- **Common exceptions** - Domain-specific exception types