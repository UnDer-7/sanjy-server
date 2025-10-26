package br.com.gorillaroxo.sanjy.server.core.domain;

import lombok.Builder;

import java.time.LocalTime;
import java.util.List;

@Builder
public record MealTypeDomain(
    Long id,
    String name,
    LocalTime scheduledTime,
    Long dietPlanId,
    String observation,
    List<StandardOptionDomain> standardOptions
) {

}
