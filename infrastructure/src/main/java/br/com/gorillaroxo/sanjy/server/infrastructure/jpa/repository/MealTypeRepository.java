package br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository;

import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.MealTypeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealTypeRepository extends CrudRepository<MealTypeEntity, Long> {

    boolean existsByIdAndDietPlanIsActiveIsTrue(Long id);
}
