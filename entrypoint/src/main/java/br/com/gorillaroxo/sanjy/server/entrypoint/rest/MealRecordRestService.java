package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealRecordRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.SearchMealRecordParamRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordCreatedResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.PageResponseMealRecordDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.OpenApiConstants;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Tag(name = "Meal Record", description = "Handles meal record operations")
public interface MealRecordRestService {

    @Operation(
        summary = "Create a new meal record",
        description = """
            Records a meal consumption with timestamp, meal type, and quantity. Can register either a standard meal \
            (following the diet plan by referencing a standard option) or a free meal (off-plan with custom description). \
            Standard meals must have standardOptionId, while free meals must have isFreeMeal=true and freeMealDescription.
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            schema = @Schema(implementation = CreateMealRecordRequestDto.class),
            examples = {
                @ExampleObject(
                    name = "Record a meal not listed in the diet plan (e.g., snacks, unplanned meals)",
                    summary = "Free meal record",
                    value = """
                         {
                             "mealTypeId": 1,
                             "consumedAt": "2025-11-14T10:34:55Z",
                             "isFreeMeal": true,
                             "standardOptionId": null,
                             "freeMealDescription": "Cookies",
                             "quantity": null,
                             "unit": null,
                             "notes": null
                         }
                        """
                ),
                @ExampleObject(
                    name = "Record a meal from a standard option defined in the diet plan",
                    summary = "Planned meal record",
                    value = """
                         {
                             "mealTypeId": 1,
                             "consumedAt": "2025-11-14T10:34:55Z",
                             "isFreeMeal": false,
                             "standardOptionId": 2,
                             "freeMealDescription": null,
                             "quantity": null,
                             "unit": null,
                             "notes": null
                         }
                        """
                )
            })))
    @ApiResponse(
        responseCode = OpenApiConstants.HttpStatusCodes.CREATED,
        description = "Meal Record successfully created",
        content =
        @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = MealRecordCreatedResponseDto.class),
            examples = {
                @ExampleObject(
                    name = "Free meal record",
                    summary = "Record a meal not listed in the diet plan (e.g., snacks, unplanned meals)",
                    value = """
                         {
                             "mealTypeId": 1,
                             "consumedAt": "2025-11-14T10:34:55Z",
                             "isFreeMeal": true,
                             "standardOptionId": null,
                             "freeMealDescription": "Cookies",
                             "quantity": null,
                             "unit": null,
                             "notes": null
                         }
                        """
                ),
                @ExampleObject(
                    name = "Planned meal record",
                    summary = "Record a meal from a standard option defined in the diet plan",
                    value = """
                         {
                             "mealTypeId": 1,
                             "consumedAt": "2025-11-14T10:34:55Z",
                             "isFreeMeal": false,
                             "standardOptionId": 2,
                             "freeMealDescription": null,
                             "quantity": null,
                             "unit": null,
                             "notes": null
                         }
                        """
                )
            }))
    @ApiResponse(
        responseCode = OpenApiConstants.HttpStatusCodes.CREATED,
        description = "Meal Record successfully created",
        content =
        @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = MealRecordCreatedResponseDto.class)))
    @ApiResponse(
        responseCode = OpenApiConstants.HttpStatusCodes.UNPROCESSABLE_ENTITY,
        description = "Business rule violation — all possible error scenarios are documented below",
        content = @Content(
            schema = @Schema(implementation = ErrorResponseDto.class),
            examples = {
                @ExampleObject(
                    name = "Meal type not found in active diet plan",
                    summary = "Meal type not found",
                    value = """
                        {
                          "code": "007",
                          "timestamp": "2026-02-15T15:16:38.479127645Z",
                          "message": "Meal Type was not found",
                          "customMessage": "MealType was not found in a active Diet Plan | MealType ID informed: 1",
                          "httpStatusCode": 422
                        }
                        """
                ),
                @ExampleObject(
                    name = "Free meal must not include standard options",
                    summary = "Free meal with standard options",
                    value = """
                        {
                          "code": "005",
                          "timestamp": "2026-02-15T15:19:45.206097370Z",
                          "message": "Meal record has invalid values",
                          "customMessage": "Free meal record cannot have standard options",
                          "httpStatusCode": 422
                        }
                        """
                ),
                @ExampleObject(
                    name = "Free meal requires a valid description",
                    summary = "Missing or invalid free meal description",
                    value = """
                        {
                          "code": "005",
                          "timestamp": "2026-02-15T15:23:09.216371216Z",
                          "message": "Meal record has invalid values",
                          "customMessage": "Free meal record has invalid meal description",
                          "httpStatusCode": 422
                        }
                        """),
                @ExampleObject(
                    name = "Planned meal must not include a free meal description",
                    summary = "Planned meal with free meal description",
                    value = """
                        {
                          "code": "005",
                          "timestamp": "2026-02-15T15:26:32.647188047Z",
                          "message": "Meal record has invalid values",
                          "customMessage": "Planned meal record cannot have free meal description",
                          "httpStatusCode": 422
                        }
                        """),
                @ExampleObject(
                    name = "Planned meal requires a valid standard option ID",
                    summary = "Invalid standard option for planned meal",
                    value = """
                        {
                          "code": "005",
                          "timestamp": "2026-02-15T15:38:10.986786753Z",
                          "message": "Meal record has invalid values",
                          "customMessage": "Planned meal record has invalid standard options",
                          "httpStatusCode": 422
                        }
                        """),
                @ExampleObject(
                    name = "Standard option not found for the given meal type",
                    summary = "Standard option not found",
                    value = """
                        {
                          "code": "008",
                          "timestamp": "2026-02-15T15:41:40.532476918Z",
                          "message": "Standard Option was not found",
                          "customMessage": "Standard Option was not found in given Meal Type | MealType ID informed: 3 - StandardOption ID informed: 1\\n",
                          "httpStatusCode": 422
                        }
                        """)
            }
        )
    )
    MealRecordCreatedResponseDto newMealRecord(@RequestBody @Valid @NotNull CreateMealRecordRequestDto request);

    @Operation(summary = "Get today's meal records", description = """
        Retrieves all meals consumed today, ordered by consumption time. Includes both standard meals (following the diet plan) \
        and free meals (off-plan). Use this to check daily food intake and diet adherence.
        """)
    @Parameter(
        name = RequestConstants.Query.TIMEZONE,
        description = "timezone using naming convention of the tz database",
        required = true,
        example = OpenApiConstants.Examples.TIMEZONE,
        in = ParameterIn.QUERY,
        schema = @Schema(implementation = String.class))
    @ApiResponse(
        responseCode = OpenApiConstants.HttpStatusCodes.OK,
        description = "Today's meal records",
        content =
        @Content(
            array = @ArraySchema(schema = @Schema(implementation = MealRecordResponseDto.class)),
            examples = {
                @ExampleObject(
                    name = "Meals found for today",
                    summary = "List with recorded meals",
                    value = """
                        [
                            {
                                "id": 129,
                                "consumedAt": "2026-02-04T07:09:09.555854Z",
                                "mealType": {
                                    "id": 2,
                                    "name": "Pre-workout snack",
                                    "scheduledTime": "06:20:00",
                                    "observation": "25g protein | 40g carbs | 3g fat | 285 kcal",
                                    "metadata": {
                                        "createdAt": "2026-01-08T12:49:09.477776Z",
                                        "updatedAt": "2026-01-08T12:49:09.477776Z"
                                    }
                                },
                                "isFreeMeal": false,
                                "standardOption": {
                                    "id": 4,
                                    "optionNumber": 1,
                                    "description": "Banana -- 1 unit (90g) | Whey protein isolate -- 30g | Oats -- 20g",
                                    "metadata": {
                                        "createdAt": "2026-01-08T12:49:09.490238Z",
                                        "updatedAt": "2026-01-08T12:49:09.490238Z"
                                    }
                                },
                                "freeMealDescription": null,
                                "quantity": 1,
                                "unit": "serving",
                                "notes": "Good energy for workout",
                                "metadata": {
                                    "createdAt": "2026-02-04T07:09:09.555854Z",
                                    "updatedAt": "2026-02-04T07:09:09.555854Z"
                                }
                            }
                        ]
                        """),
                @ExampleObject(
                    name = "No meals recorded today",
                    summary = "Empty result",
                    value = "[]")
            }))
    List<MealRecordResponseDto> getTodayMealRecords(@NotNull ZoneId timezone);

    @Operation(summary = "Search meal records with filters and pagination", description = """
        Searches meal records with pagination and optional filters (date range via consumedAtAfter/consumedAtBefore, and meal type via isFreeMeal). \
        Returns paginated results with total count. Use this to view historical meal data, analyze eating patterns, or generate reports.
        """)
    @Parameter(
        name = RequestConstants.Query.PAGE_NUMBER,
        description = "Page number to retrieve (zero-based, where 0 is the first page)",
        required = true,
        example = OpenApiConstants.Examples.ZERO,
        in = ParameterIn.QUERY,
        schema = @Schema(implementation = Integer.class))
    @Parameter(
        name = RequestConstants.Query.PAGE_SIZE,
        description = "Number of items per page. If not specified, returns 10 items per page",
        required = false,
        example = OpenApiConstants.Examples.TEN,
        in = ParameterIn.QUERY,
        schema = @Schema(implementation = Integer.class, defaultValue = "10"))
    @Parameter(
        name = RequestConstants.Query.CONSUMED_AT_AFTER,
        description = "Filter meals consumed after this date/time in UTC timezone (ISO 8601 format)",
        required = false,
        example = OpenApiConstants.Examples.DATE_TIME,
        in = ParameterIn.QUERY,
        schema =
        @Schema(
            implementation = Instant.class,
            format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT,
            defaultValue = "current day at 00:00:00"))
    @Parameter(
        name = RequestConstants.Query.CONSUMED_AT_BEFORE,
        description = "Filter meals consumed before this date/time in UTC timezone (ISO 8601 format)",
        required = false,
        example = OpenApiConstants.Examples.DATE_TIME,
        in = ParameterIn.QUERY,
        schema =
        @Schema(
            implementation = Instant.class,
            format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT,
            defaultValue = "current day at 23:59:59"))
    @Parameter(
        name = RequestConstants.Query.IS_FREE_MEAL,
        description = """
            Filter by meal type. True returns only free meals (off-plan), false returns only standard meals (following the diet plan). \
            If not specified, returns both types
            """,
        required = false,
        example = OpenApiConstants.Examples.FALSE,
        in = ParameterIn.QUERY,
        schema = @Schema(implementation = Boolean.class))
    @ApiResponse(
        responseCode = OpenApiConstants.HttpStatusCodes.OK,
        description = "Paginated meal records",
        content =
        @Content(
            schema = @Schema(implementation = PageResponseMealRecordDto.class)))
    @ApiResponse(
        responseCode = OpenApiConstants.HttpStatusCodes.OK,
        description = "Today's meal records",
        content =
        @Content(
            array = @ArraySchema(schema = @Schema(implementation = MealRecordResponseDto.class)),
            examples = {
                @ExampleObject(
                    name = "Paginated results with matching records",
                    summary = "Records found",
                    value = """
                        {
                            "totalPages": 27,
                            "currentPage": 0,
                            "pageSize": 2,
                            "totalItems": 132,
                            "content": [
                                {
                                    "id": 1,
                                    "consumedAt": "2026-01-08T22:10:13.858714Z",
                                    "mealType": {
                                        "id": 1,
                                        "name": "Breakfast",
                                        "scheduledTime": "09:30:00",
                                        "observation": "45g protein | 35g carbs | 6g fat | 380 kcal",
                                        "metadata": {
                                            "createdAt": "2026-01-08T12:55:13.835577Z",
                                            "updatedAt": "2026-01-08T12:55:13.835577Z"
                                        }
                                    },
                                    "isFreeMeal": true,
                                    "standardOption": null,
                                    "freeMealDescription": "Pancakes with syrup -- 3 units | Bacon -- 4 strips | Orange juice -- 250ml",
                                    "quantity": 1,
                                    "unit": "combo",
                                    "notes": "Weekend brunch - First day of diet plan, still adjusting",
                                    "metadata": {
                                        "createdAt": "2026-01-08T22:10:13.858714Z",
                                        "updatedAt": "2026-01-08T22:10:13.858714Z"
                                    }
                                }
                            ]
                        }
                        """),
                @ExampleObject(
                    name = "No records match the given filters",
                    summary = "Empty result",
                    value = """
                        {
                          "totalPages": 0,
                          "currentPage": 0,
                          "pageSize": 5,
                          "totalItems": 0,
                          "content": []
                        }
                        """)
            }))
    PageResponseMealRecordDto searchMealRecords(
        @Parameter(hidden = true) @NotNull @Valid SearchMealRecordParamRequestDto pageRequest);

    @Operation(summary = "Get meal record statistics by date range", description = """
        Retrieves aggregated statistics for meal records within a specified date range. Returns metrics such as total meals consumed, \
        breakdown by meal type (standard vs free meals), and nutritional totals. Use this to analyze eating patterns, track diet adherence, \
        and monitor nutritional intake over a period.
        """)
    @Parameter(
        name = RequestConstants.Query.CONSUMED_AT_AFTER,
        description = "Filter meals consumed after this date/time in UTC timezone (ISO 8601 format)",
        required = true,
        example = OpenApiConstants.Examples.DATE_TIME,
        in = ParameterIn.QUERY,
        schema =
        @Schema(
            implementation = Instant.class,
            format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT,
            defaultValue = "current day at 00:00:00"))
    @Parameter(
        name = RequestConstants.Query.CONSUMED_AT_BEFORE,
        description = "Filter meals consumed before this date/time in UTC timezone (ISO 8601 format)",
        required = true,
        example = OpenApiConstants.Examples.DATE_TIME,
        in = ParameterIn.QUERY,
        schema =
        @Schema(
            implementation = Instant.class,
            format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT,
            defaultValue = "current day at 23:59:59"))
    @ApiResponse(
        responseCode = OpenApiConstants.HttpStatusCodes.OK,
        description = "Successfully retrieved meal record statistics for the specified date range",
        content =
        @Content(
            examples = {
                @ExampleObject(
                    name = "Statistics with meal records",
                    description =
                        "Response when meal records exist within the specified date range",
                    value = """
                        {
                            "freeMealQuantity": 5,
                            "plannedMealQuantity": 15,
                            "mealQuantity": 20
                        }
                        """),
                @ExampleObject(
                    name = "Statistics without meal records",
                    description =
                        "Response when no meal records exist within the specified date range",
                    value = """
                        {
                          "freeMealQuantity": 0,
                          "plannedMealQuantity": 0,
                          "mealQuantity": 0
                        }
                        """)
            }))
    MealRecordStatisticsResponseDto getMealRecordStatisticsByDateRange(
        @RequestParam(name = RequestConstants.Query.CONSUMED_AT_AFTER, required = false) @NotNull
        Instant consumedAtAfter,
        @RequestParam(name = RequestConstants.Query.CONSUMED_AT_BEFORE, required = false) @NotNull
        Instant consumedAtBefore);

}
