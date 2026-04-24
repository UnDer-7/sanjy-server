package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateDietPlanRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.UpdateDietPlanRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.OpenApiConstants;
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

@Tag(name = "Diet Plan", description = "Handles diet plan operations")
public interface DietPlanRestService {

    @Operation(summary = "Create a new diet plan", description = """
                Creates a new diet plan with meal types (breakfast, lunch, snack, dinner, etc.), standard meal options, nutritional targets \
                (daily calories, protein, carbs, fat), and goals. The new plan is automatically set as active and any previously active plan is deactivated. \
                Each meal type can have multiple standard options for variety.
                """)
    @ApiResponse(
            responseCode = OpenApiConstants.HttpStatusCodes.CREATED,
            description = "Diet Plan successfully created",
            content = @Content(schema = @Schema(implementation = DietPlanCompleteResponseDto.class)))
    @ApiResponse(
            responseCode = OpenApiConstants.HttpStatusCodes.UNPROCESSABLE_ENTITY,
            description = "Business rule violation — all possible error scenarios are documented below",
            content =
                    @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = {
                                @ExampleObject(
                                        name = "The first standard option number must be 1",
                                        summary = "Standard options not starting from 1",
                                        value = """
                                {
                                  "code": "004",
                                  "timestamp": "2026-02-14T19:19:32.854839907Z",
                                  "message": "Standard options is not in sequence",
                                  "customMessage": "StandardOptions must start with number 1, but started with number 2 | MealType 'Breakfast' (ID: null)",
                                  "httpStatusCode": 422
                                }
                                """),
                                @ExampleObject(
                                        name = "Standard option numbers have gaps or repeated values",
                                        summary = "Non-sequential standard option numbers",
                                        value = """
                                {
                                  "code": "004",
                                  "timestamp": "2026-02-14T19:19:32.854839907Z",
                                  "message": "Standard options is not in sequence",
                                  "customMessage": "MealType 'Breakfast' (ID: null) has StandardOptions with non-sequential numbers (skipped or repeated numbers detected)",
                                  "httpStatusCode": 422
                                }
                                """),
                                @ExampleObject(
                                        name = "Two or more meal types share the same name",
                                        summary = "Duplicate meal type names",
                                        value = """
                                {
                                  "code": "006",
                                  "timestamp": "2026-02-15T06:21:00.449240797Z",
                                  "message": "Meal type names has repeated values",
                                  "customMessage": "Repeated meal type names: breakfast",
                                  "httpStatusCode": 422
                                }
                                """)
                            }))
    DietPlanCompleteResponseDto newDietPlan(@Valid @NotNull CreateDietPlanRequestDto request);

    @Operation(summary = "Get the currently active diet plan", description = """
                Retrieves the currently active diet plan with all meal types, standard options, nutritional targets (calories, protein, carbs, fat), \
                and goals. Only one diet plan can be active at a time. Use this to check the current diet configuration.
                """)
    @ApiResponse(
            responseCode = OpenApiConstants.HttpStatusCodes.OK,
            description = "Active Diet Plan",
            content = @Content(schema = @Schema(implementation = DietPlanCompleteResponseDto.class)))
    @ApiResponse(
            responseCode = OpenApiConstants.HttpStatusCodes.NOT_FOUND,
            description = "Diet Plan not found",
            content =
                    @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = {@ExampleObject(name = "No active diet plan found", value = """
                                {
                                  "code": "003",
                                  "timestamp": "2026-02-15T06:38:34.896836872Z",
                                  "message": "Diet plan was not found",
                                  "customMessage": "Could not find active diet plan",
                                  "httpStatusCode": 404
                                }
                                """)}))
    DietPlanCompleteResponseDto activeDietPlan();

    @Operation(summary = "Partially update a diet plan", description = """
                Partially updates an existing diet plan. Only the fields explicitly provided in the request body will \
                be modified — omitted, null, empty fields are ignored and existing values are preserved. \
                Meal types and standard options are not affected by this operation.
                """)
    @Parameter(
        name = RequestConstants.Path.ID,
        description = "Diet plan ID",
        required = true,
        example = OpenApiConstants.Examples.ID,
        in = ParameterIn.PATH,
        schema = @Schema(implementation = String.class)
    )
    @ApiResponse(
            responseCode = OpenApiConstants.HttpStatusCodes.OK,
            description = "Diet Plan successfully updated",
            content = @Content(schema = @Schema(implementation = DietPlanCompleteResponseDto.class)))
    @ApiResponse(
            responseCode = OpenApiConstants.HttpStatusCodes.NOT_FOUND,
            description = "Diet Plan not found",
            content =
                    @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = {@ExampleObject(name = "Diet plan not found", value = """
                                {
                                  "code": "003",
                                  "timestamp": "2026-02-15T06:38:34.896836872Z",
                                  "message": "Diet plan was not found",
                                  "customMessage": "Could not find diet plan with id 99",
                                  "httpStatusCode": 404
                                }
                                """)}))
    DietPlanCompleteResponseDto updateDietPlan(
            @Valid @NotNull UpdateDietPlanRequestDto requestBody,
            @NotNull Long id);
}
