package br.com.gorillaroxo.sanjy.infrastructure.jpa.repository;

import br.com.gorillaroxo.sanjy.infrastructure.jpa.entity.MealRecordEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MealRecordRepository extends CrudRepository<MealRecordEntity, Long> {

    List<MealRecordEntity> findByConsumedAtBetweenOrderByConsumedAt(LocalDateTime consumedAtAfter, LocalDateTime consumedAtBefore);
}
