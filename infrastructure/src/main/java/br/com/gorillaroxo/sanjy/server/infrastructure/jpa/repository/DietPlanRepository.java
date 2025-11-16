package br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository;

import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.DietPlanEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietPlanRepository extends CrudRepository<DietPlanEntity, Long> {

    @Query("""
        SELECT DISTINCT dp
        FROM DietPlanEntity dp
        LEFT JOIN FETCH dp.mealTypes mt
        LEFT JOIN FETCH mt.standardOptions
        WHERE dp.isActive = true
        """)
    Optional<DietPlanEntity> findActiveDietPlan();
}
