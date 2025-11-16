package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordStatisticsDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.MealRecordGateway;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetMealRecordStatisticsUseCase;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetMealRecordStatisticsUseCaseImpl implements GetMealRecordStatisticsUseCase {

    private final MealRecordGateway mealRecordGateway;

    @Override
    public MealRecordStatisticsDomain execute(
            final LocalDateTime consumedAtAfter, final LocalDateTime consumedAtBefore) {
        log.info(
                LogField.Placeholders.THREE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "Starting to get meal record statistics"),
                StructuredArguments.kv(LogField.CONSUMED_AT_AFTER.label(), consumedAtAfter),
                StructuredArguments.kv(LogField.CONSUMED_AT_BEFORE.label(), consumedAtBefore));

        final MealRecordStatisticsDomain mealRecordStatistics = mealRecordGateway
                .getMealRecordStatisticsByDateRange(consumedAtAfter, consumedAtBefore)
                .orElseGet(MealRecordStatisticsDomain::empty);

        log.info(
                LogField.Placeholders.SIX.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "Successfully finished getting meal record statistics"),
                StructuredArguments.kv(LogField.CONSUMED_AT_AFTER.label(), consumedAtAfter),
                StructuredArguments.kv(LogField.CONSUMED_AT_BEFORE.label(), consumedAtBefore),
                StructuredArguments.kv(LogField.FREE_MEAL_QUANTITY.label(), mealRecordStatistics.freeMealQuantity()),
                StructuredArguments.kv(
                        LogField.PLANNED_MEAL_QUANTITY.label(), mealRecordStatistics.plannedMealQuantity()),
                StructuredArguments.kv(LogField.MEAL_QUANTITY.label(), mealRecordStatistics.mealQuantity()));

        return mealRecordStatistics;
    }
}
