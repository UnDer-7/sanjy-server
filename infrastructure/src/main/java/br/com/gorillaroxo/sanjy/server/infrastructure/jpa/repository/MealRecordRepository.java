package br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository;

import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.MealRecordEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.projection.MealRecordStatisticsProjection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRecordRepository
        extends CrudRepository<MealRecordEntity, Long>, JpaSpecificationExecutor<MealRecordEntity> {

    @Query("""
            SELECT mr FROM MealRecordEntity mr
            LEFT JOIN FETCH mr.mealType mt
            LEFT JOIN FETCH mt.standardOptions
            LEFT JOIN FETCH mr.standardOption
            WHERE mr.consumedAt BETWEEN :consumedAtAfter AND :consumedAtBefore
            ORDER BY mr.consumedAt
            """)
    List<MealRecordEntity> findByConsumedAtBetweenOrderByConsumedAt(
            LocalDateTime consumedAtAfter, LocalDateTime consumedAtBefore);

    @Query("""
            SELECT new br.com.gorillaroxo.sanjy.server.infrastructure.jpa.projection.MealRecordStatisticsProjection(
                SUM(CASE WHEN mr.isFreeMeal = true THEN 1 ELSE 0 END),
                SUM(CASE WHEN mr.isFreeMeal = false THEN 1 ELSE 0 END),
                COUNT(mr)
            )
            FROM MealRecordEntity mr
            WHERE mr.consumedAt BETWEEN :startDate AND :endDate
            """)
    Optional<MealRecordStatisticsProjection> getMealRecordStatisticsByDateRange(
            LocalDateTime startDate, LocalDateTime endDate);
}
