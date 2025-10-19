package br.com.gorillaroxo.sanjy.server.core.service;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.PageResultDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.SearchMealRecordParamDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.MealRecordGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealRecordService {

    private final MealRecordGateway mealRecordGateway;

    public MealRecordDomain insert(final MealRecordDomain mealRecordDomain) {
        // todo: validar dados

        return mealRecordGateway.insert(mealRecordDomain);
    }

    public List<MealRecordDomain> searchByConsumedAt(final LocalDateTime consumedAtAfter, final LocalDateTime consumedAtBefore) {
        return mealRecordGateway.searchByConsumedAt(consumedAtAfter, consumedAtBefore);
    }

    public PageResultDomain<MealRecordDomain> search(final SearchMealRecordParamDomain searchParam) {
        return mealRecordGateway.search(searchParam);
    }
}
