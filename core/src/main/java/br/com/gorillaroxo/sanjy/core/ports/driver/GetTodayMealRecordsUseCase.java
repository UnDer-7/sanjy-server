package br.com.gorillaroxo.sanjy.core.ports.driver;

import br.com.gorillaroxo.sanjy.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.core.domain.MealRecordDomain;

import java.util.List;

public interface GetTodayMealRecordsUseCase {

    List<MealRecordDomain> execute();

}
