package br.com.gorillaroxo.sanjy.server.entrypoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;

@Builder
@Schema(description = "Request DTO for creating a new meal record - represents a single food item consumed")
public record CreateMealRecordRequestDto(
        @Schema(
                description = "ID of the meal type (breakfast, lunch, snack, dinner, etc...)",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long mealTypeId,

        @Schema(
                description = """
                    Date and time when the item was consumed. This field should only be set when registering a meal that was eaten in the past and \
                    forgotten to be logged at the time. Must be a past or present date/time (cannot be in the future). \
                    If not provided, defaults to current time.
                    """,
                example = "2025-10-13T08:30:00",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @PastOrPresent
        LocalDateTime consumedAt,

        @Schema(
                description =
                        "Indicates if this is a free meal (off-plan) or a standard meal (following the diet plan). "
                                + "TRUE = free meal | FALSE = standard meal",
                example = "false",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Boolean isFreeMeal,

        @Schema(
                description = """
                    ID of the chosen diet plan option. Required when isFreeMeal = FALSE, should be NULL when isFreeMeal = TRUE
                    """,
                example = "5",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Long standardOptionId,

        @Schema(
                description = """
                    Text description of the free meal item. Required when isFreeMeal = TRUE, should be NULL when isFreeMeal = FALSE
                    """,
                example = "Grilled chicken with vegetables",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String freeMealDescription,

        @Schema(
                description = "Quantity of the item consumed. Defaults to 1.0 if not provided",
                example = "1",
                defaultValue = "1",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        BigDecimal quantity,

        @Schema(
                description = """
                    Unit of measurement for the quantity (serving, g, ml, units, etc...). Defaults to 'serving' if not provided
                    """,
                example = "serving",
                defaultValue = "serving",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String unit,

        @Schema(
                description = "Optional field for additional observations or notes about the meal",
                example = "Extra spicy, no salt",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String notes) {

    public CreateMealRecordRequestDto {
        quantity = Objects.requireNonNullElse(quantity, BigDecimal.ONE);
        unit = Objects.requireNonNullElse(unit, "serving");
        consumedAt = Objects.requireNonNullElse(consumedAt, LocalDateTime.now());
    }
}
