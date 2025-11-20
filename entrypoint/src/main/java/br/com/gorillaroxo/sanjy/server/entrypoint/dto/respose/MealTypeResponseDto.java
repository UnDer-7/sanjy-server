package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder
@Schema(description = "Response DTO representing a meal type with its scheduled time and standard food options")
public record MealTypeResponseDto(
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
                example = "06:20:00",
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

        @Schema(
                description = "Identifier of the diet plan this meal type belongs to",
                example = "456",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long dietPlanId,

        @Schema(
                description = "Set of standard food options for this meal type",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<StandardOptionResponseDto> standardOptions) {

    public MealTypeResponseDto {
        standardOptions = Objects.requireNonNullElse(standardOptions, Collections.emptyList());
    }
}
