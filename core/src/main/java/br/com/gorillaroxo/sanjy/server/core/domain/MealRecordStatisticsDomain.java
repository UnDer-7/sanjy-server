package br.com.gorillaroxo.sanjy.server.core.domain;

import java.util.Objects;
import lombok.Builder;

@Builder
public record MealRecordStatisticsDomain(Long freeMealQuantity, Long plannedMealQuantity, Long mealQuantity) {

    public MealRecordStatisticsDomain {
        freeMealQuantity = Objects.requireNonNullElse(freeMealQuantity, 0L);
        plannedMealQuantity = Objects.requireNonNullElse(plannedMealQuantity, 0L);
        mealQuantity = Objects.requireNonNullElse(mealQuantity, 0L);
    }

    private MealRecordStatisticsDomain() {
        this(0L, 0L, 0L);
    }

    public static MealRecordStatisticsDomain empty() {
        return new MealRecordStatisticsDomain();
    }
}
