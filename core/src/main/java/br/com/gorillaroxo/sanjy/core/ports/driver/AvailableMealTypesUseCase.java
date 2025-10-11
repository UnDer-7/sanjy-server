package br.com.gorillaroxo.sanjy.core.ports.driver;

import br.com.gorillaroxo.sanjy.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.core.domain.MealTypeDomain;

import java.util.List;

public interface AvailableMealTypesUseCase {

    DietPlanDomain execute();
}
