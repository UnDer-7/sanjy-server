package br.com.gorillaroxo.sanjy.infrastructure.adapter.gateway.repository;

import br.com.gorillaroxo.sanjy.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.core.ports.driven.DietPlanGateway;
import br.com.gorillaroxo.sanjy.infrastructure.jpa.entity.DietPlanEntity;
import br.com.gorillaroxo.sanjy.infrastructure.jpa.repository.DietPlanRepository;
import br.com.gorillaroxo.sanjy.infrastructure.mapper.DietPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        final var mealTypeList = new HashSet<>(entity.getMealTypes());
        mealTypeList.forEach(mealType -> {
            mealType.setDietPlan(entity);
            final var standardOptionList = new HashSet<>(mealType.getStandardOptions());
            standardOptionList.forEach(standardOption -> standardOption.setMealType(mealType));
            mealType.setStandardOptions(standardOptionList);
        });

        entity.setMealTypes(mealTypeList);

        final DietPlanEntity savedDietPlan = dietPlanRepository.save(entity);
        return dietPlanMapper.toDomain(savedDietPlan);
    }

    @Override
    public Optional<DietPlanDomain> findActive() {
        return dietPlanRepository.findByIsActiveTrue()
            .map(dietPlanMapper::toDomain);
    }

}
