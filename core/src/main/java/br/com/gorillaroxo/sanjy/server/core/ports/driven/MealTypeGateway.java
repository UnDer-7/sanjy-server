package br.com.gorillaroxo.sanjy.server.core.ports.driven;

public interface MealTypeGateway {

    boolean existsByIdAndDietPlanActive(Long mealTypeId);
}
