package br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository;

import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.DietPlanEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DietPlanRepository extends CrudRepository<DietPlanEntity, Long> {

    @Query("""
        SELECT DISTINCT dp
        FROM DietPlanEntity dp
        LEFT JOIN FETCH dp.mealTypes mt
        LEFT JOIN FETCH mt.standardOptions so
        WHERE dp.isActive = true
        ORDER BY mt.scheduledTime ASC NULLS LAST, so.optionNumber ASC
        """)
    Optional<DietPlanEntity> findActiveDietPlanWithOrderedRelations();
}
