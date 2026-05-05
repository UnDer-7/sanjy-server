package br.com.gorillaroxo.sanjy.server.core.ports.driven;

import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import java.util.Optional;

public interface StandardOptionGateway {

    boolean existsByIdAndDietPlanActive(Long standardOptionId, Long mealTypeId);

    Optional<StandardOptionDomain> findById(Long id);

    StandardOptionDomain patch(StandardOptionDomain domain);
}
