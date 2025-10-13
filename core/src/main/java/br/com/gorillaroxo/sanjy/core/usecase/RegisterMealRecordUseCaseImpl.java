package br.com.gorillaroxo.sanjy.core.usecase;

import br.com.gorillaroxo.sanjy.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.core.ports.driver.RegisterMealRecordUseCase;
import br.com.gorillaroxo.sanjy.core.service.MealRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class RegisterMealRecordUseCaseImpl implements RegisterMealRecordUseCase {

    private final MealRecordService mealRecordService;

    @Override
    public MealRecordDomain execute(final MealRecordDomain mealRecord) {
        return mealRecordService.insert(mealRecord);
    }
}
