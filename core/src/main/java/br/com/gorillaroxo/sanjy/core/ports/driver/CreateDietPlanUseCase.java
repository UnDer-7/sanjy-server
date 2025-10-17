package br.com.gorillaroxo.sanjy.core.ports.driver;

import br.com.gorillaroxo.sanjy.core.domain.DietPlanDomain;

public interface CreateDietPlanUseCase {

    DietPlanDomain execute(DietPlanDomain dietPlan);

}
