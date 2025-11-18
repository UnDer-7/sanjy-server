package br.com.gorillaroxo.sanjy.server.core.ports.driven;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordStatisticsDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.PageResultDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.SearchMealRecordParamDomain;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MealRecordGateway {

    MealRecordDomain insert(MealRecordDomain mealRecordDomain);

    List<MealRecordDomain> searchByConsumedAt(LocalDateTime consumedAtAfter, LocalDateTime consumedAtBefore);

    PageResultDomain<MealRecordDomain> search(SearchMealRecordParamDomain searchParam);

    Optional<MealRecordStatisticsDomain> getMealRecordStatisticsByDateRange(
            LocalDateTime consumedAtAfter, LocalDateTime consumedAtBefore);
}
