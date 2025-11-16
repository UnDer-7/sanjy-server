package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetTodayMealRecordsUseCase;
import br.com.gorillaroxo.sanjy.server.core.service.MealRecordService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetTodayMealRecordsUseCaseImpl implements GetTodayMealRecordsUseCase {

    private final MealRecordService mealRecordService;

    @Override
    public List<MealRecordDomain> execute() {
        final LocalDate currentLocalDate = LocalDate.now();
        final LocalDateTime startOfDay = LocalDateTime.of(currentLocalDate, LocalTime.MIN);
        final LocalDateTime endOfDay = LocalDateTime.of(currentLocalDate, LocalTime.MAX);

        return mealRecordService.searchByConsumedAt(startOfDay, endOfDay);
    }
}
