package br.com.gorillaroxo.sanjy.core.ports.driver;

import br.com.gorillaroxo.sanjy.core.domain.MealRecordDomain;

public interface RegisterMealRecordUseCase {

    MealRecordDomain execute(final MealRecordDomain mealRecord);

}
