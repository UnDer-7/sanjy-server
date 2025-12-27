package br.com.gorillaroxo.sanjy.server.core.ports.driver;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.pagination.PageResultDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.pagination.SearchMealRecordParamDomain;

public interface SearchMealRecordUseCase {

    PageResultDomain<MealRecordDomain> execute(SearchMealRecordParamDomain searchParam);
}
