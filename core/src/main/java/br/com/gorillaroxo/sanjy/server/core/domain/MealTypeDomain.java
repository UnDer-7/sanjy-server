package br.com.gorillaroxo.sanjy.server.core.domain;

import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public record MealTypeDomain(
        Long id,
        String name,
        LocalTime scheduledTime,
        Long dietPlanId,
        String observation,
        List<StandardOptionDomain> standardOptions,
        MetadataDomain metadata) {}
