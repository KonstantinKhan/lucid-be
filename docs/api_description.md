## API Specification

The `specs` directory contains the OpenAPI 3.1.0 specification for the "Modular Task API". The specification is split
into multiple files for better organization.

```
specs/
├── openapi.yaml
├── components/
│   ├── parameters/
│   │   └── task.yaml
│   ├── responses/
│   │   └── error.yaml
│   └── schemas/
│       ├── error.yaml
│       ├── task.yaml
│       └── task-extensions.yaml
└── domains/
    └── tasks.yaml
```

| File                                                                                          | Description                                                                                                 |
|-----------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| [`openapi.yaml`](../specs/openapi.yaml)                                                       | Main entry point for the API specification. Defines basic info, server details, and references other files. |
| [`domains/tasks.yaml`](../specs/domains/tasks.yaml)                                           | Defines the endpoints related to tasks (create, list, update, delete).                                      |
| [`components/parameters/task.yaml`](../specs/components/parameters/task.yaml)                 | Defines the `taskId` path parameter.                                                                        |
| [`components/responses/error.yaml`](../specs/components/responses/error.yaml)                 | Defines standard error responses like `NotFound` and `BadRequest`.                                          |
| [`components/schemas/error.yaml`](../specs/components/schemas/error.yaml)                     | Defines the `Error` schema.                                                                                 |
| [`components/schemas/task.yaml`](../specs/components/schemas/task.yaml)                       | Defines the core `Task` model, and schemas for creating and updating tasks.                                 |
| [`components/schemas/task-extensions.yaml`](../specs/components/schemas/task-extensions.yaml) | Defines schemas for extensions to the core task model, such as priority and time tracking.                  |
