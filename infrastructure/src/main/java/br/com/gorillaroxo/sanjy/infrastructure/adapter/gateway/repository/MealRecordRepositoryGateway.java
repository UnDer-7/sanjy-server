package br.com.gorillaroxo.sanjy.infrastructure.adapter.gateway.repository;

import br.com.gorillaroxo.sanjy.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.core.ports.driven.MealRecordGateway;
import br.com.gorillaroxo.sanjy.infrastructure.jpa.entity.MealRecordEntity;
import br.com.gorillaroxo.sanjy.infrastructure.jpa.repository.MealRecordRepository;
import br.com.gorillaroxo.sanjy.infrastructure.mapper.MealRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

}
