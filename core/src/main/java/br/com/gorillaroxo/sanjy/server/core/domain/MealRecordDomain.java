package br.com.gorillaroxo.sanjy.server.core.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import lombok.Builder;

@Builder
public record MealRecordDomain(
        Long id,
        Instant consumedAt,
        MealTypeDomain mealType,
        Boolean isFreeMeal,
        StandardOptionDomain standardOption,
        String freeMealDescription,
        BigDecimal quantity,
        String unit,
        String notes,
        MetadataDomain metadata) {

    public Optional<StandardOptionDomain> getStandardOption() {
        return Optional.ofNullable(standardOption);
    }
}
