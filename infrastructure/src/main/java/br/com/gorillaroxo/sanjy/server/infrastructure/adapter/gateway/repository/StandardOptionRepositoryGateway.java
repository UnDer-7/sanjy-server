package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.gateway.repository;

import br.com.gorillaroxo.sanjy.server.core.ports.driven.StandardOptionGateway;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository.StandardOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StandardOptionRepositoryGateway implements StandardOptionGateway {

    private final StandardOptionRepository repository;

    @Override
    public boolean existsByIdAndDietPlanActive(final Long standardOptionId, final Long mealTypeId) {
        return repository.existsByIdAndMealTypeId(standardOptionId, mealTypeId);
    }
}
