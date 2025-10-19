package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.PageResultDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.SearchMealRecordParamDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.SearchMealRecordUseCase;
import br.com.gorillaroxo.sanjy.server.core.service.MealRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchMealRecordUseCaseImpl implements SearchMealRecordUseCase {

    private final MealRecordService mealRecordService;

    @Override
    public PageResultDomain<MealRecordDomain> execute(final SearchMealRecordParamDomain searchParam) {
        return mealRecordService.search(searchParam);
    }

}
