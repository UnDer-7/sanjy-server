package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateDietPlanRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Diet Plan", description = "Handles diet plan operations")
public interface DietPlanRestService {

    @Operation(summary = "Create a new diet plan", description = """
                Creates a new diet plan with meal types (breakfast, lunch, snack, dinner, etc.), standard meal options, nutritional targets \
                (daily calories, protein, carbs, fat), and goals. The new plan is automatically set as active and any previously active plan is deactivated. \
                Each meal type can have multiple standard options for variety.
                """)
    @ApiResponse(
        responseCode = "201",
        description = "Diet Plan successfully created",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DietPlanCompleteResponseDto.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "client error",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = "unexpected error occurred",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))
    )
    DietPlanCompleteResponseDto newDietPlan(@RequestBody @Valid @NotNull CreateDietPlanRequestDto request);

    @Operation(summary = "Get the currently active diet plan", description = """
                Retrieves the currently active diet plan with all meal types, standard options, nutritional targets (calories, protein, carbs, fat), \
                and goals. Only one diet plan can be active at a time. Use this to check the current diet configuration.
                """)
    @ApiResponse(
        responseCode = "200",
        description = "Active Diet Plan",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DietPlanCompleteResponseDto.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "client error",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = "unexpected error occurred",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))
    )
    DietPlanCompleteResponseDto activeDietPlan();
}
