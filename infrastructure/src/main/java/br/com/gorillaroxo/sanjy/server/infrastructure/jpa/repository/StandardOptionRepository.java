package br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository;

import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.StandardOptionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardOptionRepository extends CrudRepository<StandardOptionEntity, Long> {

    @Query("""
            SELECT COUNT(so) > 0
            FROM StandardOptionEntity so
            WHERE so.id = :standardOptionId
            AND so.mealType.id = :mealTypeId
            AND so.mealType.dietPlan.isActive = true
            """)
    boolean existsByIdAndMealTypeId(Long standardOptionId, Long mealTypeId);
}
