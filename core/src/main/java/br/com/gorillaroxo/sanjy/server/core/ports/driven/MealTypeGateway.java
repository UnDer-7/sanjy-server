package br.com.gorillaroxo.sanjy.server.core.ports.driven;

import br.com.gorillaroxo.sanjy.server.core.domain.MealTypeDomain;
import java.util.Optional;

public interface MealTypeGateway {

    boolean existsByIdAndDietPlanActive(Long mealTypeId);

    Optional<MealTypeDomain> findById(Long id);

    boolean existsByDietPlanIdAndNameAndIdNot(Long dietPlanId, String name, Long excludeId);

    MealTypeDomain patch(MealTypeDomain domain);
}
