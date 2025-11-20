package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Response DTO representing meal record statistics within a date range")
public record MealRecordStatisticsResponseDto(
        @Schema(
                description = "Quantity of free meal records (off-plan meals) consumed within the specified date range",
                example = "5",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long freeMealQuantity,

        @Schema(description = """
                    Quantity of planned meal records (following the diet plan) consumed within the specified date range
                    """, example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
        Long plannedMealQuantity,

        @Schema(description = """
                    Total quantity of meal records (both free and planned) consumed within the specified date range
                    """, example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
        Long mealQuantity) {}
