package br.com.gorillaroxo.sanjy.core.ports.driven;

import br.com.gorillaroxo.sanjy.core.domain.MealRecordDomain;

public interface MealRecordGateway {

    MealRecordDomain insert(MealRecordDomain mealRecordDomain);

}
