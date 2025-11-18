# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**SanJy Server** is a diet and meal management application built with Spring Boot 3.5.6, Java 21, and PostgreSQL. The application enables nutritionists to create diet plans and users to track meal consumption records.

## Build & Development Commands

### Prerequisites

- Java 21 (or GraalVM Java 25 for native image builds)
- Maven 3.x (or use included Maven wrapper)
- Docker (for PostgreSQL database)
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

### Environment Configuration

Environment variables are configured in `.env` file (not committed to git):
- `SANJY_SERVER_PORT` - Server port (default: 8080)
- `SANJY_SERVER_LOGGING_LEVEL` - Log level (INFO, DEBUG, etc.)
- `SANJY_SERVER_LOGGING_FILE_PATH` - Log file path
- `SANJY_SERVER_LOGGING_APPENDER` - Logging format (CONSOLE-UNSTRUCTURED, CONSOLE-STRUCTURED, etc.)

Database connection is configured in `infrastructure/src/main/resources/application.yaml`:
- Database: `diet_control` on `localhost:5432`
- Credentials: `admin_usr` / `admin_pass`

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
- **Apache PDFBox 2.0.29** - PDF processing capabilities
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
