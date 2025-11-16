package br.com.gorillaroxo.sanjy.server.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;

@Builder
public record MealRecordDomain(
        Long id,

        LocalDateTime consumedAt,

        MealTypeDomain mealType,
        Boolean isFreeMeal,
        StandardOptionDomain standardOption,
        String freeMealDescription,
        BigDecimal quantity,
        String unit,
        String notes,
        LocalDateTime createdAt) {

    public Optional<StandardOptionDomain> getStandardOption() {
        return Optional.ofNullable(standardOption);
    }
}
