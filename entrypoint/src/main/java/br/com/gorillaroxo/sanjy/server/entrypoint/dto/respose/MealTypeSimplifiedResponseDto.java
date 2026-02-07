package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import br.com.gorillaroxo.sanjy.server.entrypoint.util.OpenApiConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.Builder;

@Builder
@Schema(description = "Response DTO representing a simplified meal type")
public record MealTypeSimplifiedResponseDto(
        @Schema(
                description = "Unique identifier of the meal type",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(
                description = "Meal type name",
                example = "Breakfast",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 50)
        String name,

        @Schema(
                description = "Scheduled time for this meal",
                example = OpenApiConstants.Examples.TIME,
                type = "string",
                pattern = "^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$",
                requiredMode = Schema.RequiredMode.REQUIRED)
        LocalTime scheduledTime,

        @Schema(
                description = """
                    Additional observations about the meal type, such as target macronutrients (protein, carbs, fat in grams) and total calories (kcal)
                    """,
                example = "30 g proteína | 20 g carbo | 5 g gordura | 250 kcal",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String observation,

        @Schema(description = """
                    Metadata information containing creation and last update timestamps, along with other contextual data
                    """, requiredMode = Schema.RequiredMode.REQUIRED)
        MetadataResponseDto metadata) {}
