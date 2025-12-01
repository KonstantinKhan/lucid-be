# lucid-be-mappers

This module provides mappers for converting between domain models (using kotlinx.serialization) and transport models (using Jackson). It handles the translation of data structures between the internal domain layer and the external API layer.

## Purpose

The mappers module serves as an adapter layer between the domain models defined in `lucid-be-common` and the transport models defined in `lucid-be-transport-openapi`. This ensures clean separation of concerns where domain models can evolve independently of API contracts.

## Key Components

### Task Mappers

The primary functionality includes bidirectional mapping between `DomainTask` and `TransportTask`:

- `DomainTask.toTransport()` - Converts a domain task to a transport task
- `TransportTask.toDomain()` - Converts a transport task to a domain task
- `DomainTaskStatus.toTransport()` - Converts domain task status to transport task status
- `TransportTask.Status.toDomain()` - Converts transport task status to domain task status

### Time Conversion

The module includes helper functions for handling time conversions between different time representations:

- `Instant.toOffsetDateTime()` - Converts from Kotlinx `Instant` to Java `OffsetDateTime`
- `OffsetDateTime.toKotlinInstant()` - Converts from Java `OffsetDateTime` to Kotlinx `Instant`

**Canonical conversion pattern:**

```kotlin
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.time.OffsetDateTime
import java.time.ZoneOffset

// Instant -> OffsetDateTime (domain to transport)
private fun Instant.toOffsetDateTime(): OffsetDateTime =
    OffsetDateTime.ofInstant(this.toJavaInstant(), ZoneOffset.UTC)

// OffsetDateTime -> Instant (transport to domain)
private fun OffsetDateTime.toKotlinInstant(): Instant {
    val javaInstant: java.time.Instant = this.toInstant()
    return javaInstant.toKotlinInstant()
}
```

### Conversion Patterns

**Time Types:**
- Domain: `kotlinx.datetime.Instant` (UTC, multiplatform)
- Transport: `java.time.OffsetDateTime` (OpenAPI standard)
- Always use UTC timezone

**Collections:**
- Domain: Non-null with default empty list
- Transport: Nullable list
- Convert: `domainList.takeIf { it.isNotEmpty() }` (domain → transport)
- Convert: `transportList ?: emptyList()` (transport → domain)

**Enums:**
- Domain: UPPERCASE (e.g., `NEW`, `IN_PROGRESS`)
- Transport: snake_case (e.g., `new`, `in_progress`)
- Use `when` expressions for mapping

See [Time Type Choices ADR](../architecture-decisions.md#4-time-type-choices) for rationale.

## Dependencies

- `lucid-be-common` - Provides domain models
- `lucid-be-transport-openapi` - Provides transport models
- `kotlinx.datetime` - For time conversions

## Testing

The mappers module includes comprehensive tests using the Kotest framework to ensure correct mapping between domain and transport models. Tests use the ShouldSpec style to provide a clear, hierarchical structure:

- Contexts group related functionality (DomainTask mapping, TransportTask mapping, TaskStatus mapping, etc.)
- Each test clearly describes what behavior it's verifying
- Tests cover bidirectional mapping for all fields
- Comprehensive edge case handling (empty lists, null values)
- Status value mappings
- Time conversion accuracy
- Round-trip mapping preservation

**Build/Test**: See [Common Commands](../../CLAUDE.md#common-commands) in CLAUDE.md. Use `./gradlew :lucid-be-mappers:test` for this module.

## Usage

The mappers are extension functions that allow for easy conversion:

```kotlin
val domainTask: DomainTask = transportTask.toDomain()
val transportTask: TransportTask = domainTask.toTransport()
```

This module is a dependency of the Ktor application layer and provides conversion between internal domain representations and API transport objects when handling HTTP requests and responses.
