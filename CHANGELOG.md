# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2025-12-10

### Added

#### DevOps & Infrastructure

- Automated Docker image deployment to Docker Hub on release
  - JVM images: `under7/sanjy-server:{version}-jvm`, `under7/sanjy-server:latest-jvm`
  - GraalVM images: `under7/sanjy-server:{version}-graalvm`, `under7/sanjy-server:latest-graalvm`, `under7/sanjy-server:latest`
- Quality and security analysis in deploy pipeline (SonarCloud + Snyk)
- Optimized Docker builds with BuildKit multi-stage support
  - New fast local build targets: `make build/jvm/docker/local` and `make build/graalvm/docker/local`
  - Reuses pre-built artifacts to speed up Docker image creation

### Changed

- Updated GraalVM GitHub Actions from v1.4.2 to v1.4.4
- Enhanced Makefile help output formatting with category separators

## [1.0.0] - 2024-12-07

### Added

#### Diet Plan API

- `POST /diet-plan` - Create new diet plan with nutritional targets (daily calories, protein, carbs, fat)
  - Define multiple meal types (breakfast, lunch, dinner, snacks)
  - Add standard meal options with predefined nutritional values
  - Include nutritionist notes and dietary goals
- `GET /diet-plan/active` - Retrieve currently active diet plan with complete structure

#### Meal Record API

- `POST /meal-record` - Register meal consumption
  - Support for standard meals (pre-defined options)
  - Support for free meals (custom entries with description)
  - Automatic timestamp tracking
- `GET /meal-record/today` - Get today's consumed meals grouped by meal type
- `GET /meal-record/search` - Search meal records with filters
  - Pagination support
  - Date range filtering
  - Sort by consumption date

#### Additional Features

- Interactive Swagger UI documentation at root path (`/`)
- Spring AI MCP Server integration for Claude AI tool usage
- Single active diet plan enforcement (only one plan can be active at a time)

[1.1.0]: https://github.com/UnDer-7/sanjy-server/releases/tag/1.1.0
[1.0.0]: https://github.com/UnDer-7/sanjy-server/releases/tag/1.0.0

