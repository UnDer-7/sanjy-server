# Automated Integration Tests

This folder contains automated integration tests for the SanJy Server happy path flow.

## Test Flow

The tests follow this sequence:

1. **1-newDietPlan.bru** - Creates a new diet plan and saves dynamic IDs
2. **2-activeDietPlan.bru** - Retrieves the active diet plan
3. **3-newMealRecord.bru** - Creates a meal record using dynamic IDs and current timestamp
4. **4-getTodayMealRecords.bru** - Gets today's meal records
5. **5-searchMealRecords.bru** - Searches meal records with pagination (current day)
6. **6-getMealRecordStatistics.bru** - Gets meal record statistics (current day)

## Key Features

- **Dynamic timestamps**: All tests use the current date/time automatically via `script:pre-request`
- **Dynamic IDs**: All IDs are captured and reused across tests using `bru.setVar()`
- **Date-aware queries**: Search and statistics tests use current day range (00:00:00 to 23:59:59)
- **No hardcoded dates**: Tests will work on any day you run them

## Prerequisites

1. **Start the database:**

   ```bash
   docker compose -f local/docker-compose.yml up -d
   ```
2. **Start the server:**

   ```bash
   ./mvnw spring-boot:run -pl infrastructure
   ```

   Or if you prefer using the JAR:

   ```bash
   ./mvnw package
   java -jar infrastructure/target/sanjy-server-*.jar
   ```
3. **Ensure server is running:**

   ```bash
   curl http://localhost:8080/actuator/health
   ```

## Running Tests

### Run all tests in the tests folder

```bash
cd local/bruno/sanjy_server
bru run tests --env LOCAL
```

### Run a specific test

```bash
bru run tests/1-newDietPlan.bru --env LOCAL
```

### Run tests with output

```bash
bru run tests --env LOCAL --output results.json
```

## Test Variables

The tests use dynamic variables set via `script:pre-request` and test scripts:

### Pre-Request Variables (generated dynamically)

- `currentTimestamp` - Current date/time in ISO 8601 format (test 3)
- `consumedAtAfter` - Start of current day (tests 5 and 6)
- `consumedAtBefore` - End of current day (tests 5 and 6)

### Test Variables (saved from responses)

- `dietPlanId` - Saved from test 1, used in test 2
- `breakfastMealTypeId` - Saved from test 1, used in test 3
- `lunchMealTypeId` - Saved from test 1
- `dinnerMealTypeId` - Saved from test 1
- `standardOptionId` - Saved from test 1, used in test 3
- `mealRecordId` - Saved from test 3, used in tests 4 and 5
- `mealRecordConsumedAt` - Saved from test 3

These variables are set dynamically during test execution using:
- `bru.setVar()` in test scripts (for response data)
- `script:pre-request` (for timestamps and dates)

## How Timestamps Work

### Test 3 - newMealRecord

Uses `script:pre-request` to generate the current timestamp:

```javascript
bru.setVar("currentTimestamp", new Date().toISOString());
```

Then references it in the body:

```json
{
  "consumedAt": "{{currentTimestamp}}"
}
```

### Tests 5 & 6 - Search and Statistics

Calculate today's date range dynamically:

```javascript
const today = new Date();
const startOfDay = new Date(today.getFullYear(), today.getMonth(), today.getDate());
const endOfDay = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 23, 59, 59);

bru.setVar("consumedAtAfter", startOfDay.toISOString());
bru.setVar("consumedAtBefore", endOfDay.toISOString());
```

## Expected Results

All tests should pass with:
- ✓ Status code assertions (201 for POST, 200 for GET)
- ✓ Response structure validation
- ✓ Data consistency checks across requests
- ✓ Pagination validation
- ✓ Statistics calculation validation
- ✓ **35/35 tests passing**

## Troubleshooting

### Tests fail on first run

If tests fail, it might be because:
- Server is not running
- Database is not running
- Wrong environment selected

### ID mismatch errors

If you see ID mismatch errors, make sure you're running tests in sequence (1 → 2 → 3 → 4 → 5 → 6).

### Date/time issues

Tests automatically use the current date and time, so they should always work regardless of when you run them. The meal record is created "now" and queries search for "today's" records.

If you see no results in search/statistics:
- Check that test 3 succeeded (created the meal record)
- Verify the server timezone matches your expectations
- Check server logs for any timezone-related errors

## API Fields Used

### CreateMealRecordRequestDto

- `consumedAt` (Instant, optional): UTC timestamp in ISO 8601 format
  - If not provided, defaults to current time
  - Must be past or present (not future)
  - Format: `yyyy-MM-ddTHH:mm:ss.sssZ` (e.g., `2025-12-26T14:30:00.123Z`)

