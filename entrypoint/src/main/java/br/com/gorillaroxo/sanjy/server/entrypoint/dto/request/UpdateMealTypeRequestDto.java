package br.com.gorillaroxo.sanjy.server.entrypoint.dto.request;

import br.com.gorillaroxo.sanjy.server.entrypoint.util.OpenApiConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalTime;

@Builder
@Schema(description = """
        Request DTO for partially updating a meal type. \
        Only fields explicitly provided in the request body will be updated — \
        omitted, null, or blank fields are ignored and the existing values are preserved. \
        Standard options are not affected by this operation.
        """)
public record UpdateMealTypeRequestDto(
        @Schema(
                description = "Name/identifier of the meal type. Must be unique within the diet plan",
                example = "Breakfast",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 50)
        String name,

        @Schema(
                description = "Scheduled time for this meal",
                example = OpenApiConstants.Examples.TIME,
                type = "string",
                pattern = "^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        LocalTime scheduledTime,

        @Schema(
                description = "Additional observations about the meal type",
                example = "30 g proteína | 20 g carbo | 5 g gordura | 250 kcal",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String observation) {}
