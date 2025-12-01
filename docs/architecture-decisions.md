# Architecture Decision Records (ADR)

This document records key architectural decisions made in the lucid-be project, including the rationale and trade-offs considered.

## Table of Contents

1. [Multi-Module Architecture](#1-multi-module-architecture)
2. [Dual Serialization Strategy](#2-dual-serialization-strategy)
3. [Separate Domain and Transport Models](#3-separate-domain-and-transport-models)
4. [Time Type Choices](#4-time-type-choices)
5. [Module Dependency Principles](#5-module-dependency-principles)
6. [OpenAPI-First API Design](#6-openapi-first-api-design)

---

## 1. Multi-Module Architecture

**Status:** Accepted

**Context:**
Need to organize codebase for a Kotlin/Ktor backend with multiple concerns: HTTP handling, domain logic, API contracts, and model mapping.

**Decision:**
Use Gradle multi-module architecture with four distinct modules:
- `lucid-be-ktor-app` - HTTP application layer
- `lucid-be-common` - Domain models and business logic
- `lucid-be-transport-openapi` - API specifications and transport models
- `lucid-be-mappers` - Mappers for converting between domain and transport models

**Rationale:**
- **Separation of Concerns:** Each module has a single, well-defined responsibility
- **Reusability:** Domain models can be used in other contexts (CLI, batch jobs, different APIs)
- **Independent Evolution:** Modules can evolve independently
- **Clear Boundaries:** Explicit dependencies prevent tight coupling
- **Build Optimization:** Modules can be built and tested independently
- **Dedicated Mapping Layer:** Clean separation between domain and transport layers through dedicated mappers module

**Consequences:**
- **Positive:**
  - Clean architecture with clear boundaries
  - Easier to reason about dependencies
  - Domain logic independent of framework
  - Can swap Ktor for another framework without touching domain
  - Dedicated mapping layer for conversions between domain and transport models
- **Negative:**
  - Slightly more complex setup than monolith
  - More files to manage

---

## 2. Dual Serialization Strategy

**Status:** Accepted

**Context:**
Need serialization for both OpenAPI-generated models (transport layer) and domain models. OpenAPI Generator uses Jackson, while Ktor prefers kotlinx.serialization.

**Decision:**
Use **two serialization libraries** intentionally:
- **Jackson** for transport models (OpenAPI generated)
- **kotlinx.serialization** for domain models (ktor-app, common)

**Rationale:**
- **Tool Compatibility:** OpenAPI Generator's Kotlin generator uses Jackson by default
- **Ktor Standard:** kotlinx.serialization is the idiomatic choice for Ktor
- **Kotlin-Native:** kotlinx.serialization is Kotlin-first, more idiomatic
- **Multiplatform:** kotlinx.serialization supports Kotlin Multiplatform (future-proofing)
- **Best of Both:** Use each library where it's most appropriate

**Consequences:**
- **Positive:**
  - Tool-compatible (OpenAPI Generator works out of the box)
  - Kotlin-idiomatic for internal code
  - Clear separation between layers
  - Each library used for its strengths
- **Negative:**
  - Two serialization libraries in project
  - Need conversion between representations
  - Slightly larger dependency footprint

**Conversion Strategy:**
Mappers in `mappers` module handle conversion:
```kotlin
fun DomainModel.toTransport(): TransportModel
fun TransportModel.toDomain(): DomainModel
```

**Alternatives Considered:**
- **Jackson everywhere:** Less Kotlin-idiomatic, less multiplatform support
- **kotlinx.serialization everywhere:** Would require custom OpenAPI Generator templates
- **Manual JSON mapping:** Too much boilerplate, error-prone

---

## 3. Separate Domain and Transport Models

**Status:** Accepted

**Context:**
API contracts (OpenAPI models) serve different purposes than domain models. Should we reuse models or separate them?

**Decision:**
Maintain **separate models** for domain and transport layers:
- **Domain models** in `lucid-be-common`: Business entities with validation
- **Transport models** in `lucid-be-transport-openapi`: API contracts

**Rationale:**
- **Different Concerns:**
  - Domain: Business rules, validation, domain logic
  - Transport: API contracts, backwards compatibility, wire format
- **Independent Evolution:**
  - API can change without affecting domain
  - Domain can evolve without breaking API
- **Type Safety:** Prevents accidentally mixing concerns
- **Validation Placement:** Domain validation vs. API validation are different
- **Clean Architecture:** Follows dependency inversion principle

**Consequences:**
- **Positive:**
  - API changes don't ripple through domain
  - Domain models can have richer behavior
  - Clear boundary between layers
  - Easier to maintain backwards compatibility
  - Type system prevents mixing layers
- **Negative:**
  - Need mapper functions for conversion
  - Some duplication of structure
  - More types to maintain

**Mapping Pattern:**
Extension functions in `mappers` module:
```kotlin
// Domain has kotlinx.serialization, Instant, non-null lists
data class Task(val assigneeIds: List<String> = emptyList())

// Transport has Jackson, OffsetDateTime, nullable lists
data class Task(val assigneeIds: List<String>? = null)
```

**Alternatives Considered:**
- **Shared models:** Simpler but couples API to domain
- **DTO pattern:** Similar but less type-safe
- **Annotations on single model:** Messy, mixes concerns

---

## 4. Time Type Choices

**Status:** Accepted

**Context:**
Different time types available in JVM ecosystem. Need to choose for domain and transport layers.

**Decision:**
Use different time types for each layer:
- **Domain models:** `kotlinx.datetime.Instant`
- **Transport models:** `java.time.OffsetDateTime`

**Rationale:**

**For Domain (`kotlinx.datetime.Instant`):**
- Timezone-agnostic (UTC)
- Kotlin-multiplatform compatible
- Simpler than zoned types
- Good for storage and business logic
- No timezone confusion

**For Transport (`java.time.OffsetDateTime`):**
- OpenAPI Generator default
- Includes timezone offset information
- Standard in REST APIs
- Client can see original timezone

**Consequences:**
- **Positive:**
  - Domain doesn't need timezone complexity
  - API preserves timezone information
  - Each layer uses appropriate type
- **Negative:**
  - Need conversion in mappers
  - Two time libraries in project

**Conversion Pattern:**
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

**Alternatives Considered:**
- **java.time everywhere:** Less Kotlin-idiomatic, no multiplatform
- **kotlinx.datetime everywhere:** Would require OpenAPI Generator customization
- **Epoch milliseconds:** Less readable, error-prone

---

## 5. Module Dependency Principles

**Status:** Accepted

**Context:**
Need rules for module dependencies to maintain clean architecture.

**Decision:**
Enforce strict dependency flow:
```
lucid-be-ktor-app
├── depends on → lucid-be-common
├── depends on → lucid-be-transport-openapi
└── depends on → lucid-be-mappers

lucid-be-mappers
├── depends on → lucid-be-common
└── depends on → lucid-be-transport-openapi

lucid-be-common
└── (no dependencies on other modules)

lucid-be-transport-openapi
└── (no dependencies on other modules)
```

**Rules:**
1. **Common module** has NO dependencies on other project modules
2. **Transport module** has NO dependencies on other project modules
3. **Ktor-app module** depends on common, transport, and mappers
4. **Mappers module** depends on both common and transport (knows about both domain and transport)
5. **No circular dependencies** allowed

**Rationale:**
- **Dependency Inversion:** Domain doesn't depend on infrastructure
- **Reusability:** Common and transport are reusable independently
- **Clear Boundaries:** Dependencies flow one way
- **Testability:** Modules can be tested in isolation
- **Build Order:** Clear build dependency graph
- **Dedicated Mapping Layer:** Separates conversion logic from application logic

**Consequences:**
- **Positive:**
  - Clean architecture
  - Domain is portable
  - Clear responsibility boundaries
  - Prevents coupling
  - Dedicated mapping layer for conversions between domain and transport models
- **Negative:**
  - Must think about where code belongs
  - Cannot reference app code from domain
  - Additional module to manage

**Alternatives Considered:**
- **Bidirectional dependencies:** Creates coupling, harder to maintain
- **All code in one module:** Simpler but loses benefits of separation
- **Mappers in ktor-app module:** Would couple application logic with mapping logic

---

## 6. OpenAPI-First API Design

**Status:** Accepted

**Context:**
Need approach for defining API contracts. Should we code-first or spec-first?

**Decision:**
Use **OpenAPI-first** approach:
1. Define API in OpenAPI YAML specs
2. Generate transport models from specs
3. Implement handlers using generated models

**Rationale:**
- **Contract-First:** API contract defined independently of implementation
- **Documentation:** Spec serves as source of truth
- **Type Safety:** Generated models ensure implementation matches spec
- **Tooling:** Can generate client SDKs, docs, mocks from spec
- **Modular Specs:** Specs split into reusable components
- **Validation:** Spec can be validated before implementation

**Implementation:**
- Modular specs in `specs/` directory
- Bundled with @redocly/cli
- Generated with OpenAPI Generator
- Integrated into build process

**Consequences:**
- **Positive:**
  - API design separated from implementation
  - Generated code stays in sync with spec
  - Can review API changes in YAML diffs
  - Supports contract testing
  - Multiple language clients possible
- **Negative:**
  - Extra build step (generation)
  - Must learn OpenAPI spec format
  - Generated code not version controlled

**Alternatives Considered:**
- **Code-first:** Simpler but couples spec to implementation
- **Manual DTOs:** More work, can drift from spec
- **Framework annotations:** Less portable, tool-specific

---

## Summary

These architectural decisions collectively create a **clean, maintainable, and scalable** backend architecture:

1. **Multi-module** structure separates concerns
2. **Dual serialization** uses best tool for each job
3. **Separate models** maintain clean boundaries
4. **Type-appropriate** time handling per layer
5. **Strict dependencies** prevent coupling
6. **OpenAPI-first** ensures contract clarity
7. **Dedicated mapping layer** for conversions between domain and transport models

The architecture prioritizes **long-term maintainability** over short-term simplicity, making strategic trade-offs that benefit the project as it scales.