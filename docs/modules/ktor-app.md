# lucid-be-ktor-app

## Overview

Main HTTP server application built with Ktor framework using Netty engine. Handles all HTTP request processing for the Task API.

## Architecture

**Entry Point:** `io.ktor.server.netty.EngineMain`

**Module Configuration Sequence:**
1. Serialization - JSON content negotiation with kotlinx.serialization
2. Databases - Exposed SQL framework configuration
3. HTTP - CORS and default headers
4. Routing - API endpoint definitions

## Configuration

- **Application Config:** `src/main/resources/application.conf` (HOCON format)
  - Server port configuration (default: 8080)
  - PostgreSQL connection details
- **Logging:** `src/main/resources/logback.xml`
  - Console output with INFO level

## Key Dependencies

- **Server:** Ktor 3.3.2 with Netty engine
- **Serialization:** kotlinx.serialization for JSON
- **Database:** Exposed ORM 0.61.0, PostgreSQL 42.7.8, H2 2.3.232
- **Logging:** Logback 1.4.14
- **Testing:** JUnit with Ktor test host

## Running

Build and run the application:

```bash
# Build all modules
./gradlew build

# Run the application
./gradlew :lucid-be-ktor-app:run

# Run with custom port
PORT=9090 ./gradlew :lucid-be-ktor-app:run

# Run tests
./gradlew :lucid-be-ktor-app:test
```

## Integration with Other Modules

To use generated OpenAPI models from `lucid-be-transport-openapi`:

```kotlin
import com.khan366kos.transport.model.Task
import com.khan366kos.transport.model.TaskCreateRequest
```

The dependency is already configured in `build.gradle.kts`.

## Development Notes

- **CORS Configuration:** Currently allows all origins - suitable for development only
- **Database:** Configuration exists in `application.conf` but implementation is pending
- **Package Structure:** All sources in `com.khan366kos` package