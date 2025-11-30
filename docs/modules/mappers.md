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

Run the tests with:
```bash
./gradlew :lucid-be-mappers:test
```

## Usage

The mappers are extension functions that allow for easy conversion:

```kotlin
val domainTask: DomainTask = transportTask.toDomain()
val transportTask: TransportTask = domainTask.toTransport()
```

This module is used by the Ktor application layer to convert between internal domain representations and API transport objects when handling HTTP requests and responses.
