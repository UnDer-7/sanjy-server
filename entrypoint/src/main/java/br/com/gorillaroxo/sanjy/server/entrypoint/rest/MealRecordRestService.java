package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.PageRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.SearchMealRecordParamRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;

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
        name = "pageNumber",
        description = "Page number to retrieve (zero-based, where 0 is the first page)",
        required = true,
        example = "0",
        schema = @Schema(type = "integer")
    )
    @Parameter(
        name = "pageSize",
        description = "Number of items per page. If not specified, returns 10 items per page",
        required = false,
        example = "10",
        schema = @Schema(type = "integer", defaultValue = "10")
    )
    @Parameter(
        name = "consumedAtAfter",
        description = "Filter meals consumed after this date/time",
        required = false,
        example = "2024-01-01T00:00:00",
        schema = @Schema(type = "string", format = "date-time", defaultValue = "current day at 00:00:00")
    )
    @Parameter(
        name = "consumedAtBefore",
        description = "Filter meals consumed before this date/time",
        required = false,
        example = "2024-12-31T23:59:59",
        schema = @Schema(type = "string", format = "date-time", defaultValue = "current day at 23:59:59")
    )
    @Parameter(
        name = "isFreeMeal",
        description = "Filter by meal type. True returns only free meals (off-plan), false returns only standard meals (following the diet plan). If not specified, returns both types",
        required = false,
        example = "false",
        schema = @Schema(type = "boolean")
    )
    PageResponseDTO<MealRecordResponseDTO> searchMealRecords(
        @Parameter(hidden = true) @NotNull @Valid SearchMealRecordParamRequestDTO pageRequest
    );

}
