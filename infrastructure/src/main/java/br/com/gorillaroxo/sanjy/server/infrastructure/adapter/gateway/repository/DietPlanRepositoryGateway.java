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

import java.util.ArrayList;
import java.util.HashSet;
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
        final var mealTypeList = new ArrayList<>(entity.getMealTypes());
        mealTypeList.forEach(mealType -> {
            mealType.setDietPlan(entity);
            final var standardOptionList = new ArrayList<>(mealType.getStandardOptions());
            standardOptionList.forEach(standardOption -> standardOption.setMealType(mealType));
            mealType.setStandardOptions(standardOptionList);
        });

        entity.setMealTypes(mealTypeList);

        final DietPlanEntity savedDietPlan = dietPlanRepository.save(entity);
        return dietPlanMapper.toDomain(savedDietPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DietPlanDomain> findActive() {
        return dietPlanRepository.findActiveDietPlanWithOrderedRelations()
            .map(dietPlanMapper::toDomain);
    }

}
