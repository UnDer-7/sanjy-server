package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.gateway.repository;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.DietPlanGateway;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.DietPlanEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository.DietPlanRepository;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.DietPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
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
        final var mealTypeSet = new LinkedHashSet<>(entity.getMealTypes());
        mealTypeSet.forEach(mealType -> {
            mealType.setDietPlan(entity);
            final var standardOptionSet = new LinkedHashSet<>(mealType.getStandardOptions());
            standardOptionSet.forEach(standardOption -> standardOption.setMealType(mealType));
            mealType.setStandardOptions(standardOptionSet);
        });

        entity.setMealTypes(mealTypeSet);

        final DietPlanEntity savedDietPlan = dietPlanRepository.save(entity);
        return dietPlanMapper.toDomain(savedDietPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DietPlanDomain> findActive() {
        return dietPlanRepository.findActiveDietPlan()
            .map(dietPlanMapper::toDomain);
    }

}
