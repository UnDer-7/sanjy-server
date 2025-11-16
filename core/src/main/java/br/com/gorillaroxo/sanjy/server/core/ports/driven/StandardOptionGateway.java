package br.com.gorillaroxo.sanjy.server.core.ports.driven;

public interface StandardOptionGateway {

    boolean existsByIdAndDietPlanActive(Long standardOptionId, Long mealTypeId);
}
