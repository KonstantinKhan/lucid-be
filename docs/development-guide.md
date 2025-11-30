# Development Guide

[← Back to CLAUDE.md](../CLAUDE.md)

Practical guide for common development tasks in the lucid-be project.

## Table of Contents

- [Adding a Module](#adding-a-module)
- [Adding a Domain Model](#adding-a-domain-model)
- [Adding an API Endpoint](#adding-an-api-endpoint)
- [Creating Mappers](#creating-mappers)
- [Adding Dependencies](#adding-dependencies)
- [Testing Patterns](#testing-patterns)
- [Common Gotchas](#common-gotchas)

## Adding a Module

Follow these steps to create a new Gradle module in the project.

### Step 1: Register Module in Settings

**Location**: `settings.gradle.kts`

Add the module to the project:

```kotlin
include("module-name")
```

### Step 2: Create Directory Structure

Create the module directory and standard structure:

```bash
mkdir -p module-name/src/main/kotlin/com/khan366kos/{module-type}
mkdir -p module-name/src/main/resources
mkdir -p module-name/src/test/kotlin/com/khan366kos/{module-type}
mkdir -p module-name/src/test/resources
```

Replace `{module-type}` with the appropriate sub-package based on module purpose:
- Application module: No sub-package (just `com/khan366kos`)
- Domain module: `common/{category}` (e.g., `common/model`)
- Transport module: `transport/{category}` (e.g., `transport/model`)
- Other modules: Choose descriptive sub-package

### Step 3: Create Build Configuration

**Location**: `module-name/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization) // if using kotlinx.serialization
}

dependencies {
    // Add module dependencies
    // implementation(project(":other-module-name"))

    // Add library dependencies
    // implementation(libs.library.name)

    // Testing
    testImplementation(libs.kotlin.test.junit)
}
```

**Key decisions**:
- Include `kotlin.plugin.serialization` if module uses kotlinx.serialization
- Add project dependencies following [module dependency rules](../architecture-decisions.md#5-module-dependency-principles)
- Use version catalog references for all external libraries

### Step 4: Verify Module Setup

Build the new module to verify configuration:

```bash
./gradlew :module-name:build
```

If successful, you should see:
```
BUILD SUCCESSFUL
```

### Package Naming Conventions

Follow the project's package naming pattern:

| Module Type      | Root Package                          | Example                           |
|------------------|---------------------------------------|-----------------------------------|
| Main application | `com.khan366kos`                      | `com.khan366kos` (no sub-package) |
| Transport/API    | `com.khan366kos.transport.{category}` | `com.khan366kos.transport.model`  |
| Domain/Common    | `com.khan366kos.common.{category}`    | `com.khan366kos.common.model`     |
| Utilities        | `com.khan366kos.util.{category}`      | `com.khan366kos.util.validation`  |

### Module Dependency Guidelines

When adding dependencies to your module's `build.gradle.kts`:

**Allowed dependency flows**:
```
application-module → common-module ✓
application-module → transport-module ✓
common-module → (no project dependencies) ✓
transport-module → (no project dependencies) ✓
```

**NOT allowed**:
```
common-module → application-module ✗ (circular)
transport-module → common-module ✗ (wrong direction)
```

See [Module Dependency Principles ADR](../architecture-decisions.md#5-module-dependency-principles) for detailed rationale.

### Example: Creating a utilities module

Complete example of creating a `lucid-be-util` module:

```bash
# 1. Create directories
mkdir -p lucid-be-util/src/main/kotlin/com/khan366kos/util
mkdir -p lucid-be-util/src/test/kotlin/com/khan366kos/util

# 2. Add to settings.gradle.kts
# include("lucid-be-util")

# 3. Create lucid-be-util/build.gradle.kts
# (use template above)

# 4. Verify
./gradlew :lucid-be-util:build
```

## Adding a Domain Model

Domain models live in the `lucid-be-common` module and represent core business entities.

### Step 1: Create the Model File

Location: `lucid-be-common/src/main/kotlin/com/khan366kos/common/model/YourEntity.kt`

```kotlin
package com.khan366kos.common.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class YourEntity(
    val id: String,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    // Add other properties
) {
    init {
        // Add domain validation
        require(name.isNotBlank()) { "Name cannot be blank" }
    }
}
```

**Key Points:**
- Use `@Serializable` annotation
- All properties should be `val` (immutable)
- Use `kotlinx.datetime.Instant` for timestamps
- Add domain validation in `init` block
- Use descriptive property names

### Step 2: Create Enum (if needed)

If your entity has status/type enums, create separate files:

Location: `lucid-be-common/src/main/kotlin/com/khan366kos/common/model/YourEntityStatus.kt`

```kotlin
package com.khan366kos.common.model

import kotlinx.serialization.Serializable

@Serializable
enum class YourEntityStatus {
    ACTIVE,
    INACTIVE,
    PENDING
}
```

### Step 3: Create Tests

Location: `lucid-be-common/src/test/kotlin/com/khan366kos/common/model/YourEntityTest.kt`

```kotlin
package com.khan366kos.common.model

import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class YourEntityTest {
    @Test
    fun `entity creation with valid data succeeds`() {
        val entity = YourEntity(
            id = "test-1",
            name = "Test Entity",
            createdAt = Instant.parse("2023-11-30T10:00:00Z"),
            updatedAt = Instant.parse("2023-11-30T10:00:00Z")
        )

        assertEquals("test-1", entity.id)
        assertEquals("Test Entity", entity.name)
    }

    @Test
    fun `entity with blank name fails validation`() {
        assertFailsWith<IllegalArgumentException> {
            YourEntity(
                id = "test-1",
                name = "   ",
                createdAt = Instant.parse("2023-11-30T10:00:00Z"),
                updatedAt = Instant.parse("2023-11-30T10:00:00Z")
            )
        }
    }

    @Test
    fun `entity serialization works correctly`() {
        val entity = YourEntity(...)
        val json = Json.encodeToString(entity)
        val deserialized = Json.decodeFromString<YourEntity>(json)

        assertEquals(entity, deserialized)
    }
}
```

### Step 4: Build and Test

```bash
./gradlew :lucid-be-common:build
./gradlew :lucid-be-common:test
```

## Adding an API Endpoint

API endpoints are defined in OpenAPI specifications and implemented in the ktor-app module.

### Step 1: Update OpenAPI Specification

Location: `specs/components/schemas/your-entity.yaml`

```yaml
YourEntity:
  type: object
  properties:
    id:
      type: string
    name:
      type: string
    createdAt:
      type: string
      format: date-time
    updatedAt:
      type: string
      format: date-time
  required:
    - id
    - name
    - createdAt
    - updatedAt
```

Location: `specs/domains/your-entities.yaml`

```yaml
/your-entities:
  get:
    summary: List all entities
    responses:
      '200':
        description: Success
        content:
          application/json:
            schema:
              type: array
              items:
                $ref: '../components/schemas/your-entity.yaml#/YourEntity'
```

### Step 2: Regenerate Transport Models

```bash
./gradlew :lucid-be-transport-openapi:build
```

This generates transport models in:
`lucid-be-transport-openapi/build/generated/openapi/src/main/kotlin/com/khan366kos/transport/model/`

### Step 3: Create Mappers

See [Creating Mappers](#creating-mappers) section below.

### Step 4: Implement Route Handler

Location: `lucid-be-ktor-app/src/main/kotlin/Routing.kt`

```kotlin
fun Application.configureRouting() {
    routing {
        route("/your-entities") {
            get {
                // Implementation here
                call.respond(/* response */)
            }
        }
    }
}
```

## Creating Mappers

Mappers convert between domain models (common module) and transport models (OpenAPI generated).

### Location

`lucid-be-ktor-app/src/main/kotlin/com/khan366kos/mappers/YourEntityMappers.kt`

### Template

```kotlin
package com.khan366kos.mappers

import com.khan366kos.common.model.YourEntity as DomainEntity
import com.khan366kos.transport.model.YourEntity as TransportEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.time.OffsetDateTime
import java.time.ZoneOffset

// Domain -> Transport

fun DomainEntity.toTransport(): TransportEntity = TransportEntity(
    id = this.id,
    name = this.name,
    createdAt = this.createdAt.toOffsetDateTime(),
    updatedAt = this.updatedAt.toOffsetDateTime()
    // Map other properties
)

// Transport -> Domain

fun TransportEntity.toDomain(): DomainEntity = DomainEntity(
    id = this.id,
    name = this.name,
    createdAt = this.createdAt.toKotlinInstant(),
    updatedAt = this.updatedAt.toKotlinInstant()
    // Map other properties
)

// Time conversion helpers

private fun Instant.toOffsetDateTime(): OffsetDateTime =
    OffsetDateTime.ofInstant(this.toJavaInstant(), ZoneOffset.UTC)

private fun OffsetDateTime.toKotlinInstant(): Instant {
    val javaInstant: java.time.Instant = this.toInstant()
    return javaInstant.toKotlinInstant()
}
```

### Key Conversion Patterns

**Time Types:**
- Domain: `kotlinx.datetime.Instant`
- Transport: `java.time.OffsetDateTime`
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

## Adding Dependencies

### Step 1: Add to Version Catalog

Location: `gradle/libs.versions.toml`

```toml
[versions]
# Add version
myLibrary = "1.2.3"

[libraries]
# Add library reference
my-library = { module = "com.example:my-library", version.ref = "myLibrary" }
```

### Step 2: Use in Module

Location: `{module-name}/build.gradle.kts`

```kotlin
dependencies {
    implementation(libs.my.library)
}
```

### Step 3: Rebuild

```bash
./gradlew build
```

## Testing Patterns

### Domain Model Tests

```kotlin
class EntityTest {
    // Valid creation
    @Test
    fun `entity creation succeeds`() { }

    // Validation rules
    @Test
    fun `entity with invalid data fails`() {
        assertFailsWith<IllegalArgumentException> { }
    }

    // Serialization
    @Test
    fun `entity serialization works`() {
        val json = Json.encodeToString(entity)
        val deserialized = Json.decodeFromString<Entity>(json)
        assertEquals(entity, deserialized)
    }

    // Copy functionality
    @Test
    fun `entity copy updates fields`() {
        val updated = entity.copy(name = "New Name")
        assertEquals("New Name", updated.name)
    }
}
```

### Ktor Integration Tests

```kotlin
class RoutingTest {
    @Test
    fun `GET endpoint returns data`() = testApplication {
        client.get("/endpoint").apply {
            assertEquals(HttpStatusCode.OK, status)
            // Assert response
        }
    }
}
```

## Common Gotchas

### 1. Time Conversion Conflicts

**Problem:** Extension function name conflicts with existing method.

```kotlin
// Wrong - conflicts with OffsetDateTime.toInstant()
private fun OffsetDateTime.toInstant(): Instant =
    this.toInstant().toKotlinInstant()
```

**Solution:** Use different name or be explicit.

```kotlin
// Correct
private fun OffsetDateTime.toKotlinInstant(): Instant {
    val javaInstant: java.time.Instant = this.toInstant()
    return javaInstant.toKotlinInstant()
}
```

### 2. Module Dependencies

**Problem:** Circular dependencies between modules.

**Solution:** Follow dependency flow:
```
ktor-app → common
ktor-app → transport-openapi
common: no dependencies
transport-openapi: no dependencies
```

### 3. Serialization Annotations

**Problem:** Mixing kotlinx.serialization and Jackson annotations.

**Solution:**
- Domain models (common): Use `@Serializable` (kotlinx)
- Transport models (generated): Use `@JsonProperty` (Jackson)
- Mappers handle conversion

### 4. Generated Code

**Problem:** Editing generated OpenAPI models.

**Solution:** Never edit generated code. Instead:
1. Modify OpenAPI spec files in `specs/`
2. Regenerate: `./gradlew :lucid-be-transport-openapi:build`

### 5. Test Failures After Domain Changes

**Problem:** Tests fail after adding validation rules.

**Solution:** Update test fixtures to satisfy new validation:
```kotlin
// Before
val task = Task(id = "1", title = "")

// After adding validation
val task = Task(id = "1", title = "Valid Title")
```

## Quick Commands Reference

```bash
# Build everything
./gradlew build

# Build specific module
./gradlew :module-name:build

# Run tests
./gradlew test

# Run application
./gradlew :lucid-be-ktor-app:run

# Regenerate OpenAPI models
./gradlew :lucid-be-transport-openapi:build

# Clean build
./gradlew clean build
```

## Next Steps

- See [CLAUDE.md](../CLAUDE.md) for architectural overview
- See [architecture-decisions.md](./architecture-decisions.md) for design rationale
- See module-specific docs in `docs/modules/` for detailed information