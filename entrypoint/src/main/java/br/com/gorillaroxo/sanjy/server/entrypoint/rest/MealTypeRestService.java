package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.UpdateMealTypeRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealTypeResponseDto;
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

@Tag(name = "Meal Type", description = "Handles meal type operations")
public interface MealTypeRestService {

    @Operation(summary = "Partially update a meal type", description = """
                Partially updates an existing meal type. Only the fields explicitly provided in the request body will \
                be modified — omitted, null, or blank fields are ignored and existing values are preserved. \
                Standard options within the meal type are not affected by this operation.
                """)
    @Parameter(
            name = RequestConstants.Path.ID,
            description = "Meal type ID",
            required = true,
            example = OpenApiConstants.Examples.ID,
            in = ParameterIn.PATH,
            schema = @Schema(implementation = String.class))
    @ApiResponse(
            responseCode = OpenApiConstants.HttpStatusCodes.OK,
            description = "Meal type successfully updated",
            content = @Content(schema = @Schema(implementation = MealTypeResponseDto.class)))
    @ApiResponse(
            responseCode = OpenApiConstants.HttpStatusCodes.UNPROCESSABLE_ENTITY,
            description = "Business rule violation — all possible error scenarios are documented below",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class),
                    examples = {
                        @ExampleObject(name = "Meal type not found", value = """
                                {
                                  "code": "007",
                                  "timestamp": "2026-02-15T06:38:34.896836872Z",
                                  "message": "Meal Type was not found",
                                  "customMessage": "Could not find meal type with id 99",
                                  "httpStatusCode": 422
                                }
                                """),
                        @ExampleObject(name = "Meal type name already in use in this diet plan", value = """
                                {
                                  "code": "010",
                                  "timestamp": "2026-02-15T06:38:34.896836872Z",
                                  "message": "Meal type name already in use",
                                  "customMessage": "Meal type name 'Breakfast' is already taken within diet plan 1. Names must be unique per diet plan, but the same name can be used in different diet plans.",
                                  "httpStatusCode": 422
                                }
                                """)
                    }))
    MealTypeResponseDto updateMealType(
            @Valid @NotNull UpdateMealTypeRequestDto requestBody,
            @NotNull Long id);
}
