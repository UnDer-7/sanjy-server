package br.com.gorillaroxo.sanjy.server.core.ports.driven;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;

import java.util.Optional;

public interface DietPlanGateway {

    DietPlanDomain insert(DietPlanDomain dietPlanDomain);

    Optional<DietPlanDomain> findActive();

}
