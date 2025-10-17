package br.com.gorillaroxo.sanjy.server.core.ports.driver;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;

public interface RegisterMealRecordUseCase {

    MealRecordDomain execute(final MealRecordDomain mealRecord);

}
