# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**SanJy Server** is a diet and meal management application built with Spring Boot 3.5.6, Java 21, and PostgreSQL. The application enables nutritionists to create diet plans and users to track meal consumption records.

## Build & Development Commands

### Prerequisites

- Java 21 (or GraalVM Java 25 for native image builds)
- Maven 3.x (or use included Maven wrapper)
- Docker (for PostgreSQL database)
- For Docker Image builds:
  - Docker with BuildKit support (Docker 19.03+)
  - BuildKit is automatically enabled via Makefile targets
  - Verify BuildKit availability: `docker buildx version`
- For GraalVM Native Image builds:
  - GraalVM JDK 25 or later
  - Native development tools: `build-essential`, `zlib1g-dev`
  - SDKMAN (optional, for easy GraalVM management)

### Local Development Setup

```bash
# Start PostgreSQL database
docker compose -f local/docker-compose.yml up -d

# Build the entire project
./mvnw clean install

# Run the application
./mvnw spring-boot:run -pl infrastructure

# Run tests for a specific module
./mvnw test -pl core
./mvnw test -pl infrastructure

# Run a single test class
./mvnw test -pl core -Dtest=ExceptionCodeTest

# Package as JAR
./mvnw package
```

### Building Docker Images

**⚠️ IMPORTANT:** All Docker image build targets require **BuildKit** support (Docker 19.03+). BuildKit is automatically enabled via `DOCKER_BUILDKIT=1` in all Makefile targets, so no manual configuration is needed.

#### Verify BuildKit Availability

```bash
# Check if BuildKit is available
docker buildx version

# If not available, update Docker to version 19.03 or later
```

#### JVM Docker Builds

```bash
# Option 1: Full build (compiles everything inside Docker - slow but self-contained)
make build/jvm/docker

# Option 2: Local build (uses pre-built JAR - FAST, requires local build first)
make build/jvm          # First, build the JAR locally
make build/jvm/docker/local  # Then, build Docker image using the JAR

# Option 3: Force build without cache (for debugging)
make build/jvm/docker/force
```

#### GraalVM Docker Builds

```bash
# Option 1: Full build (compiles native image inside Docker - VERY slow but self-contained)
make build/graalvm/docker

# Option 2: Local build (uses pre-built native binary - FAST, requires local build first)
make build/graalvm      # First, build the native binary locally (takes 2-5 min)
make build/graalvm/docker/local  # Then, build Docker image using the binary

# Option 3: Force build without cache (for debugging)
make build/graalvm/docker/force
```

#### Why BuildKit?

The Dockerfiles use multi-stage builds with conditional stages (`build-full` vs `build-local`). Without BuildKit, Docker would build **all stages** even when not needed, resulting in unnecessary compilation. BuildKit intelligently skips unused stages, making local builds much faster.

**Example:** When running `make build/jvm/docker/local`, BuildKit only builds the `build-local` and `release` stages, skipping the expensive `build-full` stage entirely.

### Building GraalVM Native Image

#### System Requirements

Before building a native image, ensure you have the required native development tools:

```bash
# Install required development libraries (Ubuntu/Debian)
sudo apt-get update
sudo apt-get install build-essential zlib1g-dev

# For other distributions:
# RHEL/Fedora: sudo dnf install gcc glibc-devel zlib-devel
# Alpine: apk add build-base zlib-dev
```

#### GraalVM Installation

Option 1: Using SDKMAN (Recommended):

```bash
# Install SDKMAN if not already installed
curl -s "https://get.sdkman.io" | bash

# Install GraalVM
sdk install java 25-graal

# Use GraalVM for current shell
sdk use java 25-graal
```

Option 2: Manual download from [GraalVM website](https://www.graalvm.org/downloads/)

#### Building Native Executable

**⚠️ IMPORTANT:** Environment variables from `.env` file are **required** during the build process. The Spring Boot AOT (Ahead-of-Time) processing needs to resolve configuration placeholders at compile time.

```bash
# Ensure GraalVM is active
sdk use java 25-graal  # if using SDKMAN

# REQUIRED: Load environment variables before building
# The build will fail without these variables loaded
set -a
source .env
set +a

# Build native image (multi-module project - build from infrastructure module)
./mvnw -Pnative -pl infrastructure native:compile

# Or use the provided build script (handles env vars automatically)
./build-native.sh
```

The native executable will be created at `infrastructure/target/sanjy-server`.

**Build time:** Expect 2-5 minutes depending on system resources.

**Required environment variables for build:**
- `SANJY_SERVER_LOGGING_LEVEL` - Used by Logback configuration during AOT
- `SANJY_SERVER_LOGGING_FILE_PATH` - Required for log file path resolution
- `SANJY_SERVER_LOGGING_APPENDER` - Determines logging configuration
- `SANJY_SERVER_PORT` - Server port configuration
- `SANJY_SERVER_DATABASE_*` - Database connection settings

#### Running the Native Executable

```bash
# Load environment variables
set -a && source .env && set +a

# Run the native executable
./infrastructure/target/sanjy-server
```

#### Native Image Configuration

The project is configured for GraalVM Native Image with:
- **Plugin:** `org.graalvm.buildtools:native-maven-plugin` configured in `infrastructure/pom.xml`
- **Profile:** Spring Boot's `native` profile (automatically configured by spring-boot-starter-parent)
- **AOT Processing:** Automatic Ahead-of-Time processing via Spring Boot Maven Plugin
- **Reachability Metadata:** Automatically pulled from GraalVM Reachability Metadata Repository
- **Main Class:** `br.com.gorillaroxo.sanjy.server.infrastructure.SanJyApplication`

#### Troubleshooting Native Image Builds

Common issues:

1. **Missing environment variables:** `Could not resolve placeholder 'SANJY_SERVER_LOGGING_LEVEL'`
   - **Cause:** Environment variables not loaded during AOT processing
   - **Solution:** Load `.env` before building: `set -a && source .env && set +a`
   - **Why:** Spring Boot's AOT engine processes configuration at compile time and needs actual values
2. **Missing zlib:** `cannot find -lz`
   - **Cause:** zlib development library not installed
   - **Solution:** `sudo apt-get install zlib1g-dev build-essential`
   - **Verify:** `dpkg -l | grep zlib1g-dev`
3. **Out of memory during compilation:**
   - **Symptoms:** Process killed, "GC overhead limit exceeded"
   - **Solution:** Increase available RAM or add build arg: `-Dorg.graalvm.buildtools.memory=4G`
   - **Alternative:** Close memory-intensive applications during build
4. **Wrong Java version:**
   - **Cause:** Using regular JDK instead of GraalVM
   - **Solution:** Ensure GraalVM is active: `sdk use java 25-graal && java -version`
   - **Expected:** Output should show "Oracle GraalVM"
5. **Missing reflection configuration for JPA projections:**
   - **Symptoms:** `org.hibernate.query.SemanticException: Missing constructor for type 'YourProjection'`
   - **Cause:** JPA projection classes used in `SELECT new` queries require reflection hints
   - **Solution:** Add `@RegisterReflectionForBinding` to projection classes:

     ```java
     import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

     @RegisterReflectionForBinding
     public record YourProjection(String field1, Long field2) { }
     ```
   - **Why:** GraalVM removes unused code at compile time; reflection metadata must be explicit
6. **Spring Cloud OpenFeign AOT compilation error:** `DefaultFeignBuilderConfiguration has protected access`
   - **Symptoms:** Build fails during AOT phase with error: `org.springframework.cloud.openfeign.FeignClientsConfiguration.DefaultFeignBuilderConfiguration has protected access in org.springframework.cloud.openfeign.FeignClientsConfiguration`
   - **Cause:** Using package-qualified names in `@FeignClient` `value` or `contextId` attributes (e.g., `value = "br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.github.GitHubReposFeignClient"`)
   - **Solution:** Use simple bean names instead of package-qualified names:

     ```java
     // ❌ WRONG - Will cause AOT compilation error
     @FeignClient(
         value = "br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.github.GitHubReposFeignClient",
         url = "${api.url}"
     )

     // ✅ CORRECT - Use simple bean name
     @FeignClient(
         value = "GitHubReposFeignClient",
         url = "${api.url}"
     )
     ```
   - **Why:** AOT code generation cannot properly handle package-qualified bean names and generates code that attempts to access protected classes
   - **Reference:** [Spring Cloud OpenFeign Issue #796](https://github.com/spring-cloud/spring-cloud-openfeign/issues/796)

### Environment Configuration

Environment variables are configured in `.env` file (not committed to git):
- `SANJY_SERVER_PORT` - Server port (default: 8080)
- `SANJY_SERVER_LOGGING_LEVEL` - Log level (INFO, DEBUG, etc.)
- `SANJY_SERVER_LOGGING_FILE_PATH` - Log file path
- `SANJY_SERVER_LOGGING_APPENDER` - Logging format (CONSOLE-UNSTRUCTURED, CONSOLE-STRUCTURED, etc.)

Database connection is configured in `infrastructure/src/main/resources/application.yaml`:
- Database: `diet_control` on `localhost:5432`
- Credentials: `admin_usr` / `admin_pass`

### Maven Wrapper Configuration

The project uses Maven Wrapper with a JVM configuration file at `.mvn/jvm.config` containing:

```
--sun-misc-unsafe-memory-access=allow
```

**Why this exists:**

Maven 3.9.x and 4.0.x use Google Guice, which calls deprecated `sun.misc.Unsafe` APIs. On Java 21+, this causes warnings:

```
WARNING: sun.misc.Unsafe::staticFieldBase has been called
WARNING: Please consider reporting this to the maintainers of
         com.google.inject.internal.aop.HiddenClassDefiner
```

The JVM flag suppresses these warnings temporarily.

**Timeline (JEP 498):**
- **Java 24**: Warnings issued (current behavior)
- **Java 26+**: Will throw exceptions (BREAKING!)
- **Java 27+**: APIs completely removed

**Action required:** When upgrading to Java 26+, you MUST update to a Maven version that fixes [MNG-8760](https://issues.apache.org/jira/browse/MNG-8760). Otherwise, Maven will fail to run. Once fixed, delete `.mvn/jvm.config`.

## Architecture

This is a **multi-module Maven project** using **Hexagonal Architecture** (Ports & Adapters) with **Domain-Driven Design** principles.

### Module Structure & Dependency Flow

```
Infrastructure (Application Layer)
  ├── depends on → Entrypoint (API Contracts)
  └── depends on → Core (Domain Logic)
```

### Module Responsibilities

#### 1. Core Module (`core/`)

**Pure business logic with no framework dependencies.**

- **Domain Models** (`domain/`): Rich domain objects (DietPlanDomain, MealRecordDomain, MealTypeDomain, StandardOptionDomain)
- **Services** (`service/`): Core business logic (DietPlanService, MealRecordService)
- **Ports** (`ports/`):
  - `driver/` - Inbound contracts (UseCases) defining what the system can do
  - `driven/` - Outbound contracts (Gateways) defining external dependencies
- **UseCases** (`usecase/`): Orchestrators implementing driver ports
- **Exceptions** (`exception/`): Domain-specific exceptions with business error codes

**Key Pattern:** The core module defines interfaces but doesn't implement infrastructure concerns.

#### 2. Entrypoint Module (`entrypoint/`)

**API contracts and DTOs with no implementation.**

- **REST Interfaces** (`rest/`): REST service contracts with OpenAPI annotations
- **Request DTOs** (`dto/request/`): Input models with Jakarta Bean Validation
- **Response DTOs** (`dto/response/`): Output models for API responses

**Key Pattern:** This module only defines contracts - actual implementations are in Infrastructure.

#### 3. Infrastructure Module (`infrastructure/`)

**Framework implementations and external adapters.**

- **Controllers** (`adapter/controller/`): REST endpoints implementing entrypoint interfaces
- **Repository Gateways** (`adapter/gateway/repository/`): Implement driven ports from core
- **JPA Entities** (`jpa/entity/`): Hibernate entities for database persistence
- **Spring Data Repositories** (`jpa/repository/`): CrudRepository interfaces
- **Mappers** (`mapper/`): MapStruct interfaces for DTO ↔ Domain ↔ Entity conversions
- **Configuration** (`config/`): Spring configurations, global exception handler, MCP setup
- **Application Entry Point**: `SanJyApplication.java`

**Key Pattern:** All Spring Framework and external library dependencies live here.

### Request Flow Example

```
HTTP POST /diet-plan
  → DietPlanController (Infrastructure - inbound adapter)
    → CreateDietPlanUseCaseImpl (Core - orchestrator)
      → DietPlanService (Core - business logic)
        → DietPlanGateway (Core - port interface)
          → DietPlanRepositoryGateway (Infrastructure - outbound adapter)
            → DietPlanRepository (Infrastructure - Spring Data)
              → PostgreSQL
```

### Data Transformation Flow

```
CreateDietPlanRequestDTO (Entrypoint)
  → DietPlanDomain (Core)
    → DietPlanEntity (Infrastructure)
      → Database
```

All transformations are handled by **MapStruct** mappers with compile-time code generation.

## Exception Handling Architecture

### Exception Hierarchy

All business exceptions extend `BusinessException` and are associated with an `ExceptionCode`:

```java
enum ExceptionCode {
    UNEXPECTED_ERROR("001", "An unexpected error occurred"),
    INVALID_VALUES("002", "Invalid values"),
    DIET_PLAN_NOT_FOUND("003", "Diet plan was not found"),
    STANDARD_OPTIONS_NOT_IN_SEQUENCE("004", "Standard options is not in sequence")
}
```

### Global Exception Handler

`GlobalRestExceptionHandlerConfig` (Infrastructure) catches all exceptions and converts them to structured `ErrorResponseDTO`:

- `BusinessException` → Mapped to appropriate HTTP status via `HttpStatus` enum
- `MethodArgumentNotValidException` → 400 Bad Request
- `ConstraintViolationException` → 400 Bad Request
- All other exceptions → Converted to `UnexpectedErrorException`

All exceptions are logged with structured context (code, message, timestamp, HTTP status).

## Spring AI MCP Server Integration

This application integrates **Spring AI Model Context Protocol (MCP) Server**, enabling Claude AI to interact with the API as a tool.

### Configuration

MCP server is configured in `application.yaml`:
- Endpoint: `/mcp/messages` (Server-Sent Events)
- Type: SYNC
- Capabilities: Tools enabled

### Tool Registration

Controllers annotated with `@Tool` are automatically discovered and registered:

```java
@RestController
public class DietPlanController implements DietPlanRestService, McpToolMarker {
    @Tool(name = "activeDietPlan", description = "Get active diet plan")
    @Override
    public ResponseEntity<DietPlanCompleteResponseDTO> getActiveDietPlan() { ... }
}
```

`McpToolMarker` is a marker interface indicating beans that expose MCP tools.

## Database Schema

**PostgreSQL with pgvector extension** (though vector functionality not yet used).

### Main Tables

1. **diet_plan** - Nutritionist-created diet plans
   - Constraint: Only ONE active plan allowed (`uk_one_active_plan`)
   - Columns: name, start_date, end_date, is_active, daily macros (calories, protein, carbs, fat), goal, nutritionist_notes
2. **meal_type** - Meal types within a diet plan (breakfast, lunch, dinner, etc.)
   - FK: diet_plan_id (CASCADE delete)
   - Unique constraint: (diet_plan_id, name)
3. **standard_options** - Pre-defined meal options for each meal type
   - FK: meal_type_id (CASCADE delete)
   - Unique constraint: (meal_type_id, option_number)
4. **meal_record** - Individual meal consumption records
   - FKs: meal_type_id, standard_option_id (nullable for free meals)
   - Check constraint: Ensures either standard or free meal (mutually exclusive)

### Migration Strategy

Database schema is initialized via SQL script:
- Location: `infrastructure/src/main/resources/migrations/init.sql`
- Currently manual execution (no Flyway/Liquibase)

## Key Technologies & Libraries

- **Spring Boot 3.5.6** with Java 21
- **Spring AI 1.0.3** - MCP Server integration
- **Lombok 1.18.34** - Boilerplate reduction
- **MapStruct 1.6.2** - Type-safe object mapping
- **Spring Data JPA + Hibernate** - ORM with lazy loading enhancement
- **PostgreSQL** - Primary database
- **SpringDoc OpenAPI 2.6.0** - API documentation (Swagger UI at `/`)
- **Logstash Logback Encoder** - Structured logging
- **JUnit 5 + Mockito** - Testing framework

### Lombok Configuration

Custom Lombok settings in `lombok.config`:
- Copies `@Qualifier`, `@Value`, and `@Lazy` annotations to generated constructors
- Ensures Spring dependency injection works correctly with Lombok-generated code

## Coding Patterns & Conventions

### Language Convention

**IMPORTANT: All code, comments, commit messages, documentation, and any text in the codebase must be written in ENGLISH.**

This includes:
- Variable names, method names, class names
- Code comments and JavaDoc
- Exception messages and log messages
- Commit messages
- Documentation files
- Test names and assertions messages

### Naming Conventions

- **Domain objects**: `*Domain` suffix (e.g., `DietPlanDomain`)
- **JPA entities**: `*Entity` suffix (e.g., `DietPlanEntity`)
- **Request DTOs**: `*RequestDTO` suffix (e.g., `CreateDietPlanRequestDTO`)
- **Response DTOs**: `*ResponseDTO` suffix (e.g., `DietPlanCompleteResponseDTO`)
- **UseCases**: `*UseCase` interface, `*UseCaseImpl` implementation
- **Gateways**: `*Gateway` interface, `*RepositoryGateway` implementation
- **Services**: `*Service` class
- **Mappers**: `*Mapper` interface

### MapStruct Configuration

All mappers use:

```java
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
```

This ensures:
- Mappers are Spring beans (injectable)
- Compilation fails if any fields are unmapped (prevents accidental data loss)

### Dependency Injection

- Constructor injection is preferred (enabled by Lombok `@RequiredArgsConstructor`)
- No `@Autowired` annotations (constructor injection is implicit)

### Validation Strategy

Multi-layered validation:
1. **Input layer**: Jakarta Bean Validation on DTOs (`@NotNull`, `@NotBlank`, `@Valid`, `@Future`, etc.)
2. **Domain layer**: Manual null checks via `Objects.requireNonNull()` in services
3. **Database layer**: Constraints and foreign keys

## Testing

Tests are located in `src/test/java` within each module.

### Test Structure

- **Core module**: Unit tests for domain logic, services, and use cases
  - Uses JUnit 5 and Mockito
  - Example: `ExceptionCodeTest.java`
- **Infrastructure module**: Integration tests with Spring Boot Test
  - Tests controllers, repositories, and mappers

### Testing Guidelines

**Assertions:**
- Always use **AssertJ** for all assertions in unit tests (not JUnit assertions)
- Import AssertJ using wildcard: `import static org.assertj.core.api.Assertions.*;`

**Example:**

```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldCreateDietPlan() {
    // When
    DietPlanDomain result = dietPlanService.create(request);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Low Carb Diet");
    assertThat(result.isActive()).isTrue();
    assertThat(result.getMealTypes()).hasSize(3);
}
```

### Running Tests

```bash
# All tests
./mvnw test

# Single module
./mvnw test -pl core

# Single test class
./mvnw test -pl core -Dtest=ExceptionCodeTest

# With code coverage
./mvnw verify
```

## API Documentation

**Swagger UI** is available at the root path when the application is running:
- Swagger UI: `http://localhost:8080/`
- OpenAPI JSON: `http://localhost:8080/api-docs`

All REST endpoints have detailed OpenAPI annotations with descriptions and examples.

## Important Implementation Notes

### Date-Time Handling

**CRITICAL: This project strictly enforces UTC timezone and ISO 8601 standard for all date-time operations.**

#### Timezone Policy

- **ALL data MUST be stored in UTC timezone** in the database
- **ALL API requests MUST send date-time values in UTC timezone**
- **ALL API responses MUST return date-time values in UTC timezone**
- The application does NOT perform timezone conversions - this is the client's responsibility
- Any configuration or code that handles date-time must ensure UTC is used

#### ISO 8601 Format Standard

All date and time fields follow the **ISO 8601 standard**:

- **Date-Time format**: `yyyy-MM-dd'T'HH:mm:ss'Z'`
  - Example: `2025-01-15T14:30:00Z`
  - The `Z` suffix is MANDATORY and indicates UTC timezone
  - Java type: `Instant` or `OffsetDateTime` with UTC offset
- **Date format**: `yyyy-MM-dd`
  - Example: `2025-01-15`
  - Java type: `LocalDate`
- **Time format**: `HH:mm:ss`
  - Example: `14:30:00`
  - Java type: `LocalTime`

#### Swagger Documentation Requirements

All API endpoints with date-time fields must document:
- The exact ISO 8601 format expected
- Example values using the `OpenApiConstants` class
- Explicit mention that UTC timezone is required
- Description stating "Format: ISO 8601"

Example:

```java
@Parameter(
    description = "Consumption timestamp in UTC timezone (ISO 8601 format: yyyy-MM-dd'T'HH:mm:ss'Z')",
    example = OpenApiConstants.DATE_TIME_EXAMPLE
)
```

### Single Active Diet Plan Rule

The database enforces **only one active diet plan** via exclusion constraint. When creating a new active diet plan, the application should deactivate existing ones first.

### Meal Record Validation

Meal records must be either:
- **Standard meal**: References a `standard_option_id`
- **Free meal**: Has `is_free_meal = true` and `free_meal_description`

These are mutually exclusive (enforced by database check constraint).

### Standard Options Sequencing

Standard options must be numbered sequentially starting from 1 within each meal type. The exception `StandardOptionsNotInSequence` is thrown if this rule is violated.

### Logging Appenders

Four logging modes available via `SANJY_SERVER_LOGGING_APPENDER`:
- `CONSOLE-UNSTRUCTURED` - Human-readable console output
- `CONSOLE-STRUCTURED` - JSON console output
- `FILE-UNSTRUCTURED` - Human-readable file output
- `FILE-STRUCTURED` - JSON file output

Configuration is in `infrastructure/src/main/resources/logback-spring.xml`.

## Local Development Tools

### Bruno API Client

API test collections are located in `local/bruno/sanjy_server/`:
- REST API examples and test cases
- Alternative to Postman/Insomnia

### Docker Compose

Local PostgreSQL instance:

```bash
cd local
docker compose up -d
```

Database: `pgvector/pgvector:pg17-trixie` (PostgreSQL 17 with vector extension)

## API Endpoints Overview

### Diet Plan Management

- `GET /diet-plan/active` - Get currently active diet plan
- `POST /diet-plan` - Create new diet plan

### Meal Record Management

- `POST /meal-record` - Register meal consumption
- `GET /meal-record/search` - Search meal records with pagination
- `GET /meal-record/today` - Get today's meal records

All endpoints return structured responses with consistent error handling.

## Performance Optimizations

1. **Virtual Threads**: Enabled in Spring Boot 3.5.6 for improved concurrency
2. **Hibernate Enhancement**: Lazy loading, dirty tracking, and association management enabled
3. **Database Indexes**: Optimized for active plan lookup and date-range queries
4. **MapStruct**: Compile-time code generation (no reflection overhead)

## GraalVM Native Image Support

The project is configured for GraalVM native compilation:

```bash
./mvnw -Pnative native:compile
```

Configuration:
- Static linking with musl libc
- Output: `sanJy-server.graalvm` binary
- Max RAM: 99% of available memory
