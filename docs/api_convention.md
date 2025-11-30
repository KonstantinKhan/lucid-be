# API Specification Conventions

The OpenAPI specification for this project is designed to be modular and easy to maintain. It is split into multiple files, each with a specific purpose. This document describes the principles of its compilation.

## File Structure

The API specification is organized into the following directories:

- `specs/`: The root directory for the API specification.
  - `openapi.yaml`: The main entry point for the specification.
  - `domains/`: Contains files that define the API paths for different resource domains (e.g., tasks, users).
  - `components/`: Contains reusable components of the API, such as parameters, responses, and schemas.

## Composition Principles

### 1. Main Entry Point

The `specs/openapi.yaml` file is the primary file that assembles the entire API specification. It contains:

- Basic information about the API (title, version).
- The list of servers.
- References to the paths defined in the `domains` directory.
- References to the reusable components defined in the `components` directory.

### 2. Domain-Specific Paths

Each file in the `specs/domains/` directory corresponds to a specific resource domain (e.g., `tasks.yaml` for tasks). These files define the API paths and operations for that domain.

Using domain-specific files helps to keep the specification organized and makes it easier to find and update the endpoints for a particular resource.

### 3. Reusable Components

The `specs/components/` directory contains reusable parts of the API, which are organized into subdirectories:

- `parameters/`: Reusable API parameters (e.g., `TaskId`).
- `responses/`: Reusable API responses (e.g., `NotFound`, `BadRequest`).
- `schemas/`: Reusable data models (e.g., `Task`, `Error`).

By using reusable components, we can ensure consistency across the API and avoid code duplication. For example, the `TaskId` parameter is defined once and can be used in multiple endpoints.

## Example

Here is an example of how the files are referenced:

- `specs/openapi.yaml` references `specs/domains/tasks.yaml` to include the task-related paths.
- `specs/domains/tasks.yaml` references `specs/components/parameters/task.yaml` to use the `TaskId` parameter.
- `specs/domains/tasks.yaml` also references `specs/components/responses/error.yaml` to use the `NotFound` and `BadRequest` responses.

This modular approach makes the API specification more scalable and maintainable.
