package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.gateway.repository;

import br.com.gorillaroxo.sanjy.server.core.ports.driven.MealTypeGateway;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository.MealTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealTypeRepositoryGateway implements MealTypeGateway {

    private final MealTypeRepository mealTypeRepository;

    @Override
    public boolean existsByIdAndDietPlanActive(final Long mealTypeId) {
        return mealTypeRepository.existsByIdAndDietPlan_IsActiveIsTrue(mealTypeId);
    }
}
