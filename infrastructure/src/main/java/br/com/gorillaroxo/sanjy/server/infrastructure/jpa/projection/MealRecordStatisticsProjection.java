package br.com.gorillaroxo.sanjy.server.infrastructure.jpa.projection;

public record MealRecordStatisticsProjection(
    Long freeMealQuantity,
    Long plannedMealQuantity,
    Long mealQuantity
) {

}
