package br.com.gorillaroxo.sanjy.core.domain.plan;

import java.time.LocalTime;
import java.util.Set;

public record MealType(
    Long id,
    String name,
    LocalTime scheduledTime,
    Set<StandardOptionDomain> standardOptions
) {

}
