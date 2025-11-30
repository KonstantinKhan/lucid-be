# Dependency Management Guide

[← Back to CLAUDE.md](../../CLAUDE.md)

Guide for managing dependencies in the lucid-be project using Gradle version catalog.

## Adding a New Library

### Step 1: Add to Version Catalog

**Location**: `gradle/libs.versions.toml`

```toml
[versions]
# Add your library version
myLibrary = "1.2.3"

[libraries]
# Add library reference
my-library = { module = "com.example:my-library", version.ref = "myLibrary" }
```

### Step 2: Use in Module

**Location**: `{module-name}/build.gradle.kts`

```kotlin
dependencies {
    implementation(libs.my.library)
}
```

### Step 3: Rebuild Project

```bash
./gradlew build
```

## Module Dependencies

To depend on another project module:

```kotlin
dependencies {
    implementation(project(":module-name"))
}
```

### Allowed Dependency Flow

Follow module dependency rules to prevent circular dependencies:

```
ktor-app
├── depends on → common ✓
└── depends on → transport-openapi ✓

common
└── (no project dependencies) ✓

transport-openapi
└── (no project dependencies) ✓
```

**What's NOT allowed:**
- `common` → `ktor-app` ✗ (circular dependency)
- `common` → `transport-openapi` ✗ (violates dependency inversion)
- `transport-openapi` → `common` ✗ (violates separation)

See [Module Dependency Principles](../architecture-decisions.md#5-module-dependency-principles) for rationale.

## Common Dependencies Reference

### Kotlin & Serialization

| Dependency | Usage | Catalog Reference |
|------------|-------|-------------------|
| JUnit test framework | Unit testing | `libs.kotlin.test.junit` |
| kotlinx.serialization JSON | JSON serialization (domain models) | `libs.kotlinx.serialization.json` |
| kotlinx.datetime | Date/time handling (multiplatform) | `libs.kotlinx.datetime` |

### Ktor Server

| Dependency | Usage | Catalog Reference |
|------------|-------|-------------------|
| Ktor server core | Core server functionality | `libs.ktor.server.core` |
| Ktor Netty engine | Netty server engine | `libs.ktor.server.netty` |
| Ktor JSON serialization | JSON content negotiation | `libs.ktor.serialization.kotlinx.json` |
| Ktor test host | Integration testing | `libs.ktor.server.test.host` |

### Database

| Dependency | Usage | Catalog Reference |
|------------|-------|-------------------|
| Exposed ORM core | SQL framework core | `libs.exposed.core` |
| Exposed JDBC | JDBC support | `libs.exposed.jdbc` |
| PostgreSQL driver | PostgreSQL database | `libs.postgresql` |
| H2 database | In-memory testing | `libs.h2` |

### Logging

| Dependency | Usage | Catalog Reference |
|------------|-------|-------------------|
| Logback classic | Logging implementation | `libs.logback.classic` |

## Version Catalog Structure

**Location**: `gradle/libs.versions.toml`

### Sections

- **[versions]**: Version numbers for all dependencies
- **[libraries]**: Library definitions with version references
- **[plugins]**: Gradle plugins

### Naming Convention

Version catalog uses dot notation that maps to dash notation in TOML:

- **In code**: `libs.ktor.server.core`
- **In TOML**: `ktor-server-core = { module = "...", version.ref = "..." }`

## Dependency Scopes

### Common Scopes

- `implementation` - Required at compile and runtime, not transitive to consumers
- `api` - Required at compile and runtime, transitive to consumers
- `testImplementation` - Test dependencies only
- `compileOnly` - Compile time only, not packaged in artifact

### When to Use Each

- **Use `implementation`**: For most dependencies (default choice)
- **Use `api`**: When exposing dependency types in public API
- **Use `testImplementation`**: For test frameworks and test utilities
- **Use `compileOnly`**: For annotation processors, provided dependencies

## Troubleshooting

### Version Conflicts

**Symptom**: Build fails with version conflict errors

**Solution**:
1. Check dependency tree:
   ```bash
   ./gradlew :module-name:dependencies
   ```
2. Explicitly set version in catalog
3. Use `constraints { }` block if needed:
   ```kotlin
   dependencies {
       constraints {
           implementation("com.example:library:1.2.3") {
               because("version 1.2.2 has critical bug")
           }
       }
   }
   ```

### Module Not Found

**Symptom**: `Project ':module-name' not found` error

**Solution**:
1. Verify module registered in `settings.gradle.kts`:
   ```kotlin
   include("module-name")
   ```
2. Check module name matches exactly: `project(":exact-module-name")`
3. Rebuild project:
   ```bash
   ./gradlew clean build
   ```

### Generated Code Missing

**Symptom**: Cannot resolve transport model classes

**Solution**:
1. Regenerate transport models:
   ```bash
   ./gradlew :lucid-be-transport-openapi:build
   ```
2. Ensure `lucid-be-transport-openapi` is built before `lucid-be-ktor-app`
3. Check `build/generated/openapi/` directory exists

### Unresolved Reference

**Symptom**: IDE shows "Unresolved reference" for dependency

**Solution**:
1. Reload Gradle project (IntelliJ: Gradle → Reload All Gradle Projects)
2. Invalidate caches and restart IDE
3. Check dependency is in correct catalog section
4. Verify module's `build.gradle.kts` includes dependency

## Best Practices

### 1. Always Use Version Catalog

**Do**:
```kotlin
implementation(libs.ktor.server.core)
```

**Don't**:
```kotlin
implementation("io.ktor:ktor-server-core:3.3.2")  // Hardcoded version
```

### 2. Keep Versions Centralized

All versions should be in `libs.versions.toml`, not scattered in build files.

### 3. Group Related Versions

For libraries that must use the same version:

```toml
[versions]
ktor = "3.3.2"

[libraries]
ktor-server-core = { module = "io.ktor:ktor-server-core", version.ref = "ktor" }
ktor-server-netty = { module = "io.ktor:ktor-server-netty", version.ref = "ktor" }
```

### 4. Document Why

Add comments for non-obvious dependency choices:

```toml
[versions]
# Using 2.7.8 instead of 3.x for compatibility with Exposed 0.61.0
postgresql = "42.7.8"
```

## See Also

- [CLAUDE.md](../../CLAUDE.md) - Project navigation hub
- [Development Guide](../development-guide.md#adding-dependencies) - Step-by-step how-to
- [Conventions Guide](./conventions.md) - Development standards
- [Module Documentation](../modules/) - Per-module dependency information