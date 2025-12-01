# lucid-be-transport-openapi

[← Back to CLAUDE.md](../../CLAUDE.md)

## Quick Reference

| Property          | Value                                               |
|-------------------|-----------------------------------------------------|
| **Purpose**       | OpenAPI spec bundling & model generation            |
| **Generator**     | OpenAPI Generator 7.7.0 (kotlin)                    |
| **Bundler**       | @redocly/cli                                        |
| **Package**       | `com.khan366kos.transport.model`                    |
| **Source Specs**  | `specs/` directory (modular YAML)                   |
| **Output**        | `build/generated/openapi/` (not version controlled) |
| **Build**         | `./gradlew :lucid-be-transport-openapi:build`       |
| **Serialization** | Jackson (`@JsonProperty`)                           |

**Build Pipeline:**

1. `npmInstall` - Install @redocly/cli
2. `bundleOpenApi` - Combine specs → `specs/bundled-openapi.yaml`
3. `openApiGenerate` - Generate Kotlin models

**Generated Models:** Task, TaskCreateRequest, TaskUpdateRequest, Error

---

## Overview

Module responsible for bundling modular OpenAPI specifications and generating Kotlin data classes for the API transport
layer. Ensures type-safe models that match the API specification.

**OpenAPI spec structure**: See [API Specification](../api_description.md) for file organization and [API Conventions](../api_convention.md) for composition principles.

## Build Process

The module executes a 3-stage build process:

1. **NPM Dependencies** - Installs @redocly/cli for specification bundling
2. **OpenAPI Bundling** - Combines modular YAML files from `specs/` into single `specs/bundled-openapi.yaml` using
   Redocly CLI
3. **Code Generation** - Generates Kotlin models from bundled specification using OpenAPI Generator

## Generated Models

Based on the current OpenAPI specification, the following models are generated:

- `Task` - Main task entity
- `TaskCreateRequest` - Request model for task creation
- `TaskUpdateRequest` - Request model for task updates
- `Error` - Standard error response model

## Key Configuration

- **OpenAPI Generator Version:** 7.7.0
- **Generator Type:** `kotlin`
- **Serialization Library:** `jackson`
- **Package:** `com.khan366kos.transport.model`
- **Output Directory:** `build/generated/openapi/`

## Gradle Tasks

- `npmInstall` - Install npm dependencies for bundling
- `bundleOpenApi` - Bundle modular specs into single file
- `openApiGenerate` - Generate Kotlin models from specification
- `build` - Runs all tasks automatically

## Development Notes

### Generated Code Location

Generated code is placed in `build/generated/openapi/` and is **not** version controlled. Always regenerate after:

- OpenAPI specification changes
- Clean builds
- Fresh repository clones

### Model Regeneration

After updating OpenAPI specs in `specs/` directory, regenerate models. See [Common Commands](../../CLAUDE.md#common-commands) in CLAUDE.md. Use `./gradlew :lucid-be-transport-openapi:build` for this module.

### Serialization Library

This module uses Jackson for serialization (OpenAPI Generator standard), while the main application uses
kotlinx.serialization (Ktor standard). Both approaches coexist intentionally for compatibility.

### Manual Edits

Do not manually edit generated models. All changes should be made to the OpenAPI specification files.