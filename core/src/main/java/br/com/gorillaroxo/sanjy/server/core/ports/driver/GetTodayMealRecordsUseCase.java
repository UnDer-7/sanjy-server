package br.com.gorillaroxo.sanjy.server.core.ports.driver;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import java.util.List;

public interface GetTodayMealRecordsUseCase {

    List<MealRecordDomain> execute();
}
