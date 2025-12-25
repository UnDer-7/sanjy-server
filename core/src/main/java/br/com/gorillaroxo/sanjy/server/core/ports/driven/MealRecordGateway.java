package br.com.gorillaroxo.sanjy.server.core.ports.driven;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordStatisticsDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.pagination.PageResultDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.pagination.SearchMealRecordParamDomain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MealRecordGateway {

    MealRecordDomain insert(MealRecordDomain mealRecordDomain);

    List<MealRecordDomain> searchByConsumedAt(Instant consumedAtAfter, Instant consumedAtBefore);

    PageResultDomain<MealRecordDomain> search(SearchMealRecordParamDomain searchParam);

    Optional<MealRecordStatisticsDomain> getMealRecordStatisticsByDateRange(
            Instant consumedAtAfter, Instant consumedAtBefore);
}
