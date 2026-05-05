package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.gateway.repository;

import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.StandardOptionGateway;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.StandardOptionEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository.StandardOptionRepository;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.StandardOptionMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StandardOptionRepositoryGateway implements StandardOptionGateway {

    private final StandardOptionRepository repository;
    private final StandardOptionMapper standardOptionMapper;

    @Override
    public boolean existsByIdAndDietPlanActive(final Long standardOptionId, final Long mealTypeId) {
        return repository.existsByIdAndMealTypeId(standardOptionId, mealTypeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StandardOptionDomain> findById(final Long id) {
        return repository.findById(id).map(standardOptionMapper::toDomain);
    }

    @Override
    @Transactional
    public StandardOptionDomain patch(final StandardOptionDomain domain) {
        final StandardOptionEntity managed = repository.findById(domain.id()).orElseThrow();
        managed.setDescription(domain.description());
        return standardOptionMapper.toDomain(repository.save(managed));
    }
}
