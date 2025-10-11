package br.com.gorillaroxo.sanjy.entrypoint.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Builder
public record CreateMealRecordRequestDTO(
    @NotNull Long mealTypeId,
    @NotNull Boolean isFreeMeal,
    Long standardOptionId,
    String freeMealDescription,
    BigDecimal quantity,
    String unit,
    String notes
) {

    public CreateMealRecordRequestDTO {
        quantity = Objects.requireNonNullElse(quantity, BigDecimal.ONE);
        unit = Objects.requireNonNullElse(unit, "serving");
    }
}
