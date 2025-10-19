package br.com.gorillaroxo.sanjy.server.entrypoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

@Schema(description = "Request DTO for creating a diet plan by the nutritionist")
public record CreateDietPlanRequestDTO(

    @NotBlank
    @Schema(description = "Name/identifier of the diet plan",
            example = "Plan N°02 - Cutting",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100)
    String name,

    @Schema(description = "Date when this diet plan starts. If not provided, defaults to current date",
            example = "2025-01-15",
            format = "yyyy-MM-dd",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalDate startDate,

    @Schema(description = "Date when this diet plan ends. If not provided, defaults to current date + 2 months. If provided, must be a future date",
            example = "2025-04-15",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Future
    LocalDate endDate,

    @Schema(description = "Target daily calories",
            example = "2266",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Integer dailyCalories,

    @Schema(description = "Target daily protein in grams",
            example = "186",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Integer dailyProteinInG,

    @Schema(description = "Target daily carbohydrates in grams",
            example = "288",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Integer dailyCarbsInG,

    @Schema(description = "Target daily fat in grams",
            example = "30",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Integer dailyFatInG,

    @Schema(description = "Main goal of this diet plan",
            example = "Body fat reduction with muscle mass preservation",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String goal,

    @Schema(description = "Additional notes or observations from the nutritionist",
            example = "Patient has lactose intolerance. Avoid dairy products.",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String nutritionistNotes,

    @Valid
    @NotNull
    @NotEmpty
    @Schema(description = "Set of meal types associated with this diet plan",
        requiredMode = Schema.RequiredMode.REQUIRED)
    Set<CreateMealTypesRequestDTO> mealTypes
) {

}
