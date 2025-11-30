# Testing Guide

This document describes the testing practices and approaches used in the lucid-be project.

## Testing Frameworks

The project uses two primary testing frameworks:

1. **Kotlin Test** - For basic unit tests (using `kotlin.test`)
2. **Kotest** - For more advanced testing scenarios with descriptive test specifications

## Testing Modules

### Common Module
- Uses Kotlin's built-in test framework
- Located in `src/test/kotlin/` directories
- Contains domain model validation tests

### Mappers Module  
- Uses Kotest framework for more descriptive tests
- Located in `lucid-be-mappers/src/test/kotlin/com/khan366kos/mappers`
- Contains comprehensive mapping tests for all bidirectional conversions

## Test Organization

Tests are organized by module and function:

```
src/test/kotlin/com/khan366kos/
├── common/
│   └── model/
│       └── TaskTest.kt
└── mappers/
    └── TaskMappersTest.kt
```

## Writing Tests

### For Kotlin Test Framework
```kotlin
@Test
fun `test description`() {
    // Test logic here
    assertEquals(expected, actual)
}
```

### For Kotest Framework
```kotlin
class MyTest : ShouldSpec({
    context("context description") {
        should("test description") {
            // Test logic here
            actual shouldBe expected
        }
    }
})
```

## Running Tests

To run all tests in the project:
```bash
./gradlew test
```

To run tests for a specific module:
```bash
./gradlew :lucid-be-mappers:test
```

To run tests with more verbose output:
```bash
./gradlew test --info
```

## Test Coverage

Aim for comprehensive coverage of:
- Domain model validation logic
- Mapping functions between domain and transport models
- Edge cases and error conditions
- Round-trip conversions to ensure data integrity