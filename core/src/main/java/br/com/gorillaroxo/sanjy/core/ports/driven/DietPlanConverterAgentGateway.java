package br.com.gorillaroxo.sanjy.core.ports.driven;

import br.com.gorillaroxo.sanjy.core.domain.plan.DietPlanDomain;

public interface DietPlanConverterAgentGateway {

    DietPlanDomain convert(final String inputMessage);

}
