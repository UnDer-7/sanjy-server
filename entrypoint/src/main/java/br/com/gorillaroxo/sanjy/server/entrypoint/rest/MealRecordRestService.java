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
        description = "Records a food item consumption with timestamp, meal type, and details. " +
                      "Can register either a standard meal (following the diet plan) or a free meal (off-plan). " +
                      "Standard meals must reference a standard option, while free meals require a description."
    )
    MealRecordResponseDTO newMealRecord(@RequestBody @Valid @NotNull CreateMealRecordRequestDTO request);

    @Operation(
        summary = "Get today's meal records",
        description = "Retrieves all meal records consumed today, ordered by consumption time. " +
                      "Includes both standard meals (following the diet plan) and free meals (off-plan)."
    )
    List<MealRecordResponseDTO> getTodayMealRecords();

    @Operation(
        summary = "Search meal records with filters and pagination",
        description = "Searches meal records with optional filters for date range and meal type. " +
                      "Results are paginated and can be filtered by consumption date/time and free meal status."
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
        description = "Filter meals consumed after this date/time. If not specified, defaults to the start of current day (00:00:00)",
        required = false,
        example = "2024-01-01T00:00:00",
        schema = @Schema(type = "string", format = "date-time", defaultValue = "current day at 00:00:00")
    )
    @Parameter(
        name = "consumedAtBefore",
        description = "Filter meals consumed before this date/time. If not specified, defaults to the end of current day (23:59:59)",
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
