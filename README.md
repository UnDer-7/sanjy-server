# SanJy Server

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=UnDer-7_sanjy-server&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=UnDer-7_sanjy-server)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=UnDer-7_sanjy-server&metric=coverage)](https://sonarcloud.io/summary/new_code?id=UnDer-7_sanjy-server)
[![Known Vulnerabilities](https://snyk.io/test/github/under-7/sanjy-server/badge.svg)](https://snyk.io/test/github/{username}/{repo})

## Development

### Build

#### Prerequisites

- Java 25+
- Maven 3.x (or use included Maven wrapper)
- Docker with BuildKit support (Docker 19.03+)

#### Local Build

```bash
# Build the entire project
./mvnw clean install

# Build JVM artifact
make build/jvm

# Build GraalVM native binary (requires GraalVM JDK 25+)
make build/graalvm
```

#### Docker

**⚠️ Important:** Docker image builds require **BuildKit** (Docker 19.03+). BuildKit is automatically enabled via Makefile targets.

**Verify BuildKit availability:**

```bash
docker buildx version
```

**JVM Docker Images:**

```bash
# Full build (compiles inside Docker - slow)
make build/jvm/docker

# Local build (uses pre-built JAR - fast)
make build/jvm
make build/jvm/docker/local

# Force build without cache
make build/jvm/docker/force
```

**GraalVM Docker Images:**

```bash
# Full build (compiles inside Docker - very slow)
make build/graalvm/docker

# Local build (uses pre-built native binary - fast)
make build/graalvm
make build/graalvm/docker/local

# Force build without cache
make build/graalvm/docker/force
```

**Available commands:**

```bash
# See all available make targets
make help
```

## Date-Time Handling

**IMPORTANT: This project strictly follows UTC timezone and ISO 8601 standard for all date-time operations.**

### Timezone Policy

- **ALL data is stored in UTC timezone** in the database
- **ALL API requests must send date-time values in UTC timezone**
- **ALL API responses return date-time values in UTC timezone**
- The application does not perform timezone conversions - clients are responsible for converting to/from their local timezone

### ISO 8601 Format

All date and time fields in the API follow the **ISO 8601 standard**:

- **Date-Time format**: `yyyy-MM-dd'T'HH:mm:ss'Z'`
  - Example: `2025-01-15T14:30:00Z`
  - The `Z` suffix indicates UTC timezone
- **Date format**: `yyyy-MM-dd`
  - Example: `2025-01-15`
- **Time format**: `HH:mm:ss`
  - Example: `14:30:00`

### API Documentation

The Swagger UI (available at `http://localhost:8080/`) provides complete documentation for all endpoints, including:
- Expected date-time formats for each field
- Example values following ISO 8601 standard
- Timezone requirements

