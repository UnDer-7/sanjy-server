package br.com.gorillaroxo.sanjy.infrastructure.adapter.gateway.repository;

import br.com.gorillaroxo.sanjy.core.domain.plan.DietPlanDomain;
import br.com.gorillaroxo.sanjy.core.ports.driven.DietPlanGateway;
import br.com.gorillaroxo.sanjy.infrastructure.jpa.entity.DietPlanEntity;
import br.com.gorillaroxo.sanjy.infrastructure.jpa.repository.DietPlanRepository;
import br.com.gorillaroxo.sanjy.infrastructure.mapper.DietPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DietPlanRepositoryGateway implements DietPlanGateway {

    private final DietPlanMapper dietPlanMapper;
    private final DietPlanRepository dietPlanRepository;

    @Override
    public DietPlanDomain insert(final DietPlanDomain dietPlanDomain) {
        final DietPlanEntity entity = dietPlanMapper.toEntity(dietPlanDomain);

        // set relations
        entity.getMealTypes().forEach(mealType -> {
            mealType.setDietPlan(entity);
            mealType.getStandardOptions().forEach(opt -> opt.setMealType(mealType));
        });

        final DietPlanEntity savedDietPlan = dietPlanRepository.save(entity);
        return dietPlanMapper.toDomain(savedDietPlan);
    }

    @Override
    public Optional<DietPlanDomain> findActive() {
        return dietPlanRepository.findByIsActiveTrue()
            .map(dietPlanMapper::toDomain);
    }

}
