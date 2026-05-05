package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.UpdateStandardOptionRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.StandardOptionResponseDto;
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

@Tag(name = "Standard Option", description = "Handles standard option operations")
public interface StandardOptionRestService {

    @Operation(summary = "Partially update a standard option", description = """
                Partially updates an existing standard meal option. Only the fields explicitly provided in the request body will \
                be modified — omitted, null, or blank fields are ignored and existing values are preserved.
                """)
    @Parameter(
            name = RequestConstants.Path.ID,
            description = "Standard option ID",
            required = true,
            example = OpenApiConstants.Examples.ID,
            in = ParameterIn.PATH,
            schema = @Schema(implementation = String.class))
    @ApiResponse(
            responseCode = OpenApiConstants.HttpStatusCodes.OK,
            description = "Standard option successfully updated",
            content = @Content(schema = @Schema(implementation = StandardOptionResponseDto.class)))
    @ApiResponse(
            responseCode = OpenApiConstants.HttpStatusCodes.UNPROCESSABLE_ENTITY,
            description = "Standard option not found",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class),
                    examples = {
                        @ExampleObject(name = "Standard option not found", value = """
                                {
                                  "code": "008",
                                  "timestamp": "2026-02-15T06:38:34.896836872Z",
                                  "message": "Standard Option was not found",
                                  "customMessage": "Could not find standard option with id 99",
                                  "httpStatusCode": 422
                                }
                                """)
                    }))
    StandardOptionResponseDto updateStandardOption(
            @Valid @NotNull UpdateStandardOptionRequestDto requestBody,
            @NotNull Long id);
}
