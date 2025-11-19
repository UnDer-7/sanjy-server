package br.com.gorillaroxo.sanjy.server.core.ports.driver;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordStatisticsDomain;
import java.time.LocalDateTime;

public interface GetMealRecordStatisticsUseCase {

    MealRecordStatisticsDomain execute(LocalDateTime consumedAtAfter, LocalDateTime consumedAtBefore);
}
