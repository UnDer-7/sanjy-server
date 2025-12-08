# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

[1.0.0]: https://github.com/UnDer-7/sanjy-server/releases/tag/1.0.0
