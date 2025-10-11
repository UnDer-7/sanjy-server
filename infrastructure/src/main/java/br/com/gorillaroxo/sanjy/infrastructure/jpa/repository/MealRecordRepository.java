package br.com.gorillaroxo.sanjy.infrastructure.jpa.repository;

import br.com.gorillaroxo.sanjy.infrastructure.jpa.entity.MealRecordEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRecordRepository extends CrudRepository<MealRecordEntity, Long> {

}
