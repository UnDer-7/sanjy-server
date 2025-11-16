package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(
        description =
                "Complete response DTO representing a diet plan with all its details, nutritional targets, and associated meal types")
public record DietPlanCompleteResponseDTO(
        @Schema(
                description = "Unique identifier of the Diet Plan",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(
                description = "Name/identifier of the diet plan",
                example = "Plan N°02 - Cutting",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 100)
        String name,

        @Schema(
                description = "Date when this diet plan starts",
                example = "2025-01-15",
                format = "yyyy-MM-dd",
                requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate startDate,

        @Schema(
                description = "Date when this diet plan ends",
                example = "2025-04-15",
                requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate endDate,

        @Schema(
                description = "Target daily calories",
                example = "2266",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer dailyCalories,

        @Schema(
                description = "Target daily protein in grams",
                example = "186",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer dailyProteinInG,

        @Schema(
                description = "Target daily carbohydrates in grams",
                example = "288",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer dailyCarbsInG,

        @Schema(
                description = "Target daily fat in grams",
                example = "30",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer dailyFatInG,

        @Schema(
                description = "Main goal of this diet plan",
                example = "Body fat reduction with muscle mass preservation",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String goal,

        @Schema(
                description = "Additional notes or observations from the nutritionist",
                example = "Patient has lactose intolerance. Avoid dairy products.",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String nutritionistNotes,

        @Schema(
                description = "List of meal types associated with this diet plan",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<MealTypeResponseDTO> mealTypes,

        @Schema(
                description = "Indicates whether this diet plan is currently active",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isActive,

        @Schema(
                description = "Timestamp when this diet plan was created",
                example = "2025-01-10T14:30:00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt) {}
