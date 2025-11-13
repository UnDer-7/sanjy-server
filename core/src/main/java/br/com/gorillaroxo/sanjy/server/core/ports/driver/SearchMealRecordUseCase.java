package br.com.gorillaroxo.sanjy.server.core.ports.driver;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.PageResultDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.SearchMealRecordParamDomain;

public interface SearchMealRecordUseCase {

    PageResultDomain<MealRecordDomain> execute(SearchMealRecordParamDomain searchParam);

}
