package br.com.gorillaroxo.sanjy.infrastructure.jpa.repository;

import br.com.gorillaroxo.sanjy.infrastructure.jpa.entity.DietPlanEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DietPlanRepository extends CrudRepository<DietPlanEntity, Long> {

    Optional<DietPlanEntity> findByIsActiveTrue();
}
