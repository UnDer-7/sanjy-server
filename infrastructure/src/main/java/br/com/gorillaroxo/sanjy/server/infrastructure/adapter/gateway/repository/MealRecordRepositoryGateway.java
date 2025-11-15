package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.gateway.repository;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordStatisticsDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.PageResultDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.SearchMealRecordParamDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.MealRecordGateway;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.MealRecordEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository.MealRecordRepository;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.MealRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealRecordRepositoryGateway implements MealRecordGateway {

    private final MealRecordRepository mealRecordRepository;
    private final MealRecordMapper mealRecordMapper;

    @Override
    public MealRecordDomain insert(final MealRecordDomain mealRecordDomain) {
        final MealRecordEntity entity = mealRecordMapper.toEntity(mealRecordDomain);
        final MealRecordEntity saved = mealRecordRepository.save(entity);
        return mealRecordMapper.toDomain(saved);
    }

    @Override
    public List<MealRecordDomain> searchByConsumedAt(final LocalDateTime consumedAtAfter, final LocalDateTime consumedAtBefore) {
        final List<MealRecordEntity> mealRecords = mealRecordRepository.findByConsumedAtBetweenOrderByConsumedAt(consumedAtAfter, consumedAtBefore);
        return mealRecordMapper.toDomain(mealRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResultDomain<MealRecordDomain> search(final SearchMealRecordParamDomain searchParam) {
        final var propConsumedAt = "consumedAt";
        final var pageRequest = PageRequest.of(searchParam.getPageNumber(), searchParam.getPageSize(), Sort.by(propConsumedAt).ascending());

        final Specification<MealRecordEntity> specification = (entity, cq, cb) -> {
            var predicates = cb.conjunction();

            if (searchParam.getConsumedAtAfter() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(entity.get(propConsumedAt), searchParam.getConsumedAtAfter()));
            }

            if (searchParam.getConsumedAtBefore() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(entity.get(propConsumedAt), searchParam.getConsumedAtBefore()));
            }

            if (searchParam.getIsFreeMeal() != null) {
                predicates = cb.and(predicates, cb.equal(entity.get("isFreeMeal"), searchParam.getIsFreeMeal()));
            }

            return predicates;
        };

        final var page = mealRecordRepository.findAll(specification, pageRequest);
        final var mealRecordDomains = mealRecordMapper.toDomain(page.getContent());

        return new PageResultDomain<>(
            (long) page.getTotalPages(),
            (long) page.getNumber(),
            (long) page.getSize(),
            page.getTotalElements(),
            mealRecordDomains
        );

    }

    @Override
    public Optional<MealRecordStatisticsDomain> getMealRecordStatisticsByDateRange(final LocalDateTime consumedAtAfter, final LocalDateTime consumedAtBefore) {
        return mealRecordRepository.getMealRecordStatisticsByDateRange(consumedAtAfter, consumedAtBefore)
            .map(mealRecordMapper::toDomain);
    }
}
