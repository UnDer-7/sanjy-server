package br.com.gorillaroxo.sanjy.core.ports.driver;

import br.com.gorillaroxo.sanjy.core.domain.MealRecordDomain;

public interface RegisterMealRecordUseCase {

    void execute(final MealRecordDomain mealRecord);

}
