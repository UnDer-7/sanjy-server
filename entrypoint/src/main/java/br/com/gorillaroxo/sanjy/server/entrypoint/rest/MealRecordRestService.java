package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.SearchMealRecordParamRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordStatisticsResponseDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.PageResponseDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Tag(
    name = "Meal Record",
    description = "Handles meal record operations"
)
public interface MealRecordRestService {

    @Operation(
        summary = "Create a new meal record",
        description = "Records a meal consumption with timestamp, meal type, and quantity. " +
                      "Can register either a standard meal (following the diet plan by referencing a standard option) or a free meal (off-plan with custom description). " +
                      "Standard meals must have standardOptionId, while free meals must have isFreeMeal=true and freeMealDescription."
    )
    MealRecordResponseDTO newMealRecord(@RequestBody @Valid @NotNull CreateMealRecordRequestDTO request);

    @Operation(
        summary = "Get today's meal records",
        description = "Retrieves all meals consumed today, ordered by consumption time. " +
                      "Includes both standard meals (following the diet plan) and free meals (off-plan). " +
                      "Use this to check daily food intake and diet adherence."
    )
    List<MealRecordResponseDTO> getTodayMealRecords();

    @Operation(
        summary = "Search meal records with filters and pagination",
        description = "Searches meal records with pagination and optional filters (date range via consumedAtAfter/consumedAtBefore, and meal type via isFreeMeal). " +
                      "Returns paginated results with total count. Use this to view historical meal data, analyze eating patterns, or generate reports."
    )
    @Parameter(
        name = RequestConstants.Query.PAGE_NUMBER,
        description = "Page number to retrieve (zero-based, where 0 is the first page)",
        required = true,
        example = "0",
        in = ParameterIn.QUERY,
        schema = @Schema(type = "integer")
    )
    @Parameter(
        name = RequestConstants.Query.PAGE_SIZE,
        description = "Number of items per page. If not specified, returns 10 items per page",
        required = false,
        example = "10",
        in = ParameterIn.QUERY,
        schema = @Schema(type = "integer", defaultValue = "10")
    )
    @Parameter(
        name = RequestConstants.Query.CONSUMED_AT_AFTER,
        description = "Filter meals consumed after this date/time",
        required = false,
        example = "2024-01-01T00:00:00",
        in = ParameterIn.QUERY,
        schema = @Schema(type = "string", format = "date-time", defaultValue = "current day at 00:00:00")
    )
    @Parameter(
        name = RequestConstants.Query.CONSUMED_AT_BEFORE,
        description = "Filter meals consumed before this date/time",
        required = false,
        example = "2024-12-31T23:59:59",
        in = ParameterIn.QUERY,
        schema = @Schema(type = "string", format = "date-time", defaultValue = "current day at 23:59:59")
    )
    @Parameter(
        name = RequestConstants.Query.IS_FREE_MEAL,
        description = "Filter by meal type. True returns only free meals (off-plan), false returns only standard meals (following the diet plan). If not specified, returns both types",
        required = false,
        example = "false",
        in = ParameterIn.QUERY,
        schema = @Schema(type = "boolean")
    )
    PageResponseDTO<MealRecordResponseDTO> searchMealRecords(
        @Parameter(hidden = true) @NotNull @Valid SearchMealRecordParamRequestDTO pageRequest
    );

    @Operation(
        summary = "Get meal record statistics by date range",
        description = "Retrieves aggregated statistics for meal records within a specified date range. " +
                      "Returns metrics such as total meals consumed, breakdown by meal type (standard vs free meals), " +
                      "and nutritional totals. Use this to analyze eating patterns, track diet adherence, and monitor nutritional intake over a period."
    )
    @Parameter(
        name = RequestConstants.Query.CONSUMED_AT_AFTER,
        description = "Filter meals consumed after this date/time",
        required = true,
        example = "2024-01-01T00:00:00",
        in = ParameterIn.QUERY,
        schema = @Schema(type = "string", format = "date-time", defaultValue = "current day at 00:00:00")
    )
    @Parameter(
        name = RequestConstants.Query.CONSUMED_AT_BEFORE,
        description = "Filter meals consumed before this date/time",
        required = true,
        example = "2024-12-31T23:59:59",
        in = ParameterIn.QUERY,
        schema = @Schema(type = "string", format = "date-time", defaultValue = "current day at 23:59:59")
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved meal record statistics for the specified date range",
        content = @Content(
            examples = {
                @ExampleObject(
                    name = "Statistics with meal records",
                    description = "Response when meal records exist within the specified date range",
                    value = """
                        {
                          "freeMealQuantity": 5,
                          "plannedMealQuantity": 15,
                          "mealQuantity": 20
                        }
                        """
                ),
                @ExampleObject(
                    name = "Statistics without meal records",
                    description = "Response when no meal records exist within the specified date range",
                    value = """
                        {
                          "freeMealQuantity": 0,
                          "plannedMealQuantity": 0,
                          "mealQuantity": 0
                        }
                        """
                )
            }
        )
    )
    MealRecordStatisticsResponseDTO getMealRecordStatisticsByDateRange(
        @RequestParam(name = RequestConstants.Query.CONSUMED_AT_AFTER, required = false) @NotNull final LocalDateTime consumedAtAfter,
        @RequestParam(name = RequestConstants.Query.CONSUMED_AT_BEFORE, required = false) @NotNull final LocalDateTime consumedAtBefore);
}
