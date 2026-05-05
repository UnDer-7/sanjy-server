package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.gateway.repository;

import br.com.gorillaroxo.sanjy.server.core.domain.MealTypeDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.MealTypeGateway;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.MealTypeEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository.MealTypeRepository;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.MealTypeMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealTypeRepositoryGateway implements MealTypeGateway {

    private final MealTypeRepository mealTypeRepository;
    private final MealTypeMapper mealTypeMapper;

    @Override
    public boolean existsByIdAndDietPlanActive(final Long mealTypeId) {
        return mealTypeRepository.existsByIdAndDietPlanIsActiveIsTrue(mealTypeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MealTypeDomain> findById(final Long id) {
        return mealTypeRepository.findById(id).map(mealTypeMapper::toDomain);
    }

    @Override
    public boolean existsByDietPlanIdAndNameAndIdNot(final Long dietPlanId, final String name, final Long excludeId) {
        return mealTypeRepository.existsByDietPlanIdAndNameAndIdNot(dietPlanId, name, excludeId);
    }

    @Override
    @Transactional
    public MealTypeDomain patch(final MealTypeDomain domain) {
        final MealTypeEntity managed = mealTypeRepository.findById(domain.id()).orElseThrow();
        managed.setName(domain.name());
        managed.setScheduledTime(domain.scheduledTime());
        managed.setObservation(domain.observation());
        return mealTypeMapper.toDomain(mealTypeRepository.save(managed));
    }
}
