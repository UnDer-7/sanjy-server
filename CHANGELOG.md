# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

> **Note:** Version 0.x is considered pre-alpha. The project is under active development and testing.
> Breaking changes may occur in any release, including minor versions.

<br>

> **Important:** Follow the standard [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) structure (`## [X.Y.Z] - YYYY-MM-DD`).
> The CI/CD pipeline automatically extracts the changelog section for each version to create the GitHub Release notes.

---

## [0.1.1] - 2026-02-11

### Fixed

#### SonarQube & Code Quality

- SonarQube warnings and small code fixes

---

## [0.1.0] - 2026-07-02

Initial pre-alpha release for testing and validation. This version focuses on establishing the core API
structure and verifying the end-to-end flow of diet plan management and meal tracking.

### Added

#### Diet Plan API

- `POST /v1/diet-plan` - Create a new diet plan with nutritional targets (daily calories, protein, carbs, fat)
  - Define multiple meal types (breakfast, lunch, dinner, snacks)
  - Add standard meal options with predefined nutritional values
  - Include nutritionist notes and dietary goals
  - Automatically sets the new plan as active and deactivates any previously active plan
- `GET /v1/diet-plan/active` - Retrieve the currently active diet plan with complete structure

#### Meal Record API

- `POST /v1/meal-record` - Register meal consumption
  - Support for standard meals (pre-defined diet plan options)
  - Support for free meals (custom entries with description)
  - Automatic UTC timestamp tracking
- `GET /v1/meal-record/today` - Get today's consumed meals ordered by consumption time
- `GET /v1/meal-record` - Search meal records with filters
  - Pagination support
  - Date range filtering (`consumedAtAfter` / `consumedAtBefore`)
  - Filter by meal type (`isFreeMeal`)
- `GET /v1/meal-record/statistics` - Aggregated statistics for meal records within a date range

#### Maintenance API

- `GET /v1/maintenance/project-info` - Retrieve project version, timezone configuration, and runtime mode

#### Additional Features

- Interactive Swagger UI documentation at root path (`/`)
- Spring AI MCP Server integration for Claude AI tool usage
- Single active diet plan enforcement (only one plan can be active at a time)
- All endpoints versioned under `/v1/` prefix

[0.1.1]: https://github.com/UnDer-7/sanjy-server/releases/tag/0.1.1
[0.1.0]: https://github.com/UnDer-7/sanjy-server/releases/tag/0.1.0

