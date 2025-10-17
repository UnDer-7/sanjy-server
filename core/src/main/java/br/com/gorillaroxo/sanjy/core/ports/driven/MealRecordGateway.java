package br.com.gorillaroxo.sanjy.core.ports.driven;

import br.com.gorillaroxo.sanjy.core.domain.MealRecordDomain;

import java.time.LocalDateTime;
import java.util.List;

public interface MealRecordGateway {

    MealRecordDomain insert(MealRecordDomain mealRecordDomain);

    List<MealRecordDomain> searchByConsumedAt(LocalDateTime consumedAtAfter, LocalDateTime consumedAtBefore);

}
