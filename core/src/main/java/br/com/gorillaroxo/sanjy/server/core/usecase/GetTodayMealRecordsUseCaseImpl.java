package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetTodayMealRecordsUseCase;
import br.com.gorillaroxo.sanjy.server.core.service.MealRecordService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class GetTodayMealRecordsUseCaseImpl implements GetTodayMealRecordsUseCase {

    private final MealRecordService mealRecordService;

    @Override
    public List<MealRecordDomain> execute(final ZoneId userTimezone) {
        final LocalDate currentLocalDate = LocalDate.now(userTimezone);

        final Instant startOfDay =
                currentLocalDate.atTime(LocalTime.MIN).atZone(userTimezone).toInstant();
        final Instant endOfDay =
                currentLocalDate.atTime(LocalTime.MAX).atZone(userTimezone).toInstant();

        return mealRecordService.searchByConsumedAt(startOfDay, endOfDay);
    }
}
