package br.com.gorillaroxo.sanjy.server.entrypoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Schema(description = "Request DTO for creating a meal type within a diet plan")
public record CreateMealTypesRequestDto(
        @NotBlank
        @Schema(
                description = "Meal type name. Must be unique within the diet plan's meal types list "
                        + "(case-insensitive comparison, trimmed of leading/trailing whitespace)",
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

        @Valid
        @NotNull
        @NotEmpty
        @Schema(
                description = "List of standard food options for this meal type",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<CreateStandardOptionRequestDto> standardOptions) {

    public CreateMealTypesRequestDto {
        standardOptions = Objects.requireNonNullElseGet(standardOptions, ArrayList::new);
    }
}
