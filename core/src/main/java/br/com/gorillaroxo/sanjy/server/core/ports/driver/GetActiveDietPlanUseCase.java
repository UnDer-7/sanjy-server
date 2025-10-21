package br.com.gorillaroxo.sanjy.server.core.ports.driver;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;

public interface GetActiveDietPlanUseCase {

    DietPlanDomain execute();
}
