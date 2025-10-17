package br.com.gorillaroxo.sanjy.server.core.ports.driven;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;

public interface DietPlanConverterAgentGateway {

    DietPlanDomain convert(final String inputMessage);

}
