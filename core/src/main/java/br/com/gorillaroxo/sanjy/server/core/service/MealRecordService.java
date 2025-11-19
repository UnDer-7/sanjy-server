package br.com.gorillaroxo.sanjy.server.core.service;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.PageResultDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.SearchMealRecordParamDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.core.exception.InvalidMealRecordException;
import br.com.gorillaroxo.sanjy.server.core.exception.MealTypeNotFoundException;
import br.com.gorillaroxo.sanjy.server.core.exception.StandardOptionNotFoundException;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.MealRecordGateway;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.MealTypeGateway;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.StandardOptionGateway;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealRecordService {

    private final MealRecordGateway mealRecordGateway;
    private final MealTypeGateway mealTypeGateway;
    private final StandardOptionGateway standardOptionGateway;

    public MealRecordDomain insert(final MealRecordDomain mealRecord) {
        log.info(
                LogField.Placeholders.SIX.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Starting to insert new meal record"),
                StructuredArguments.kv(LogField.MEAL_RECORD_CONSUMED_AT.label(), mealRecord.consumedAt()),
                StructuredArguments.kv(
                        LogField.MEAL_RECORD_MEAL_TYPE_ID.label(),
                        mealRecord.mealType().id()),
                StructuredArguments.kv(LogField.MEAL_RECORD_IS_FREE_MEAL.label(), mealRecord.isFreeMeal()),
                StructuredArguments.kv(
                        LogField.MEAL_RECORD_STANDARD_OPTION_ID.label(),
                        mealRecord
                                .getStandardOption()
                                .map(StandardOptionDomain::id)
                                .orElse(null)),
                StructuredArguments.kv(
                        LogField.MEAL_RECORD_FREE_MEAL_DESCRIPTION.label(), mealRecord.freeMealDescription()));

        if (!mealTypeGateway.existsByIdAndDietPlanActive(mealRecord.mealType().id())) {
            log.warn(
                    LogField.Placeholders.FOUR.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "MealType was not found in a active Diet Plan"),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_MEAL_TYPE_ID.label(),
                            mealRecord.mealType().id()),
                    StructuredArguments.kv(LogField.MEAL_RECORD_IS_FREE_MEAL.label(), mealRecord.isFreeMeal()),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_FREE_MEAL_DESCRIPTION.label(), mealRecord.freeMealDescription()));

            throw new MealTypeNotFoundException("MealType was not found in a active Diet Plan | MealType ID informed: "
                    + mealRecord.mealType().id());
        }

        if (Boolean.TRUE.equals(mealRecord.isFreeMeal())) {
            validateFreeMealRecord(mealRecord);
        } else {
            validatePlannedMealRecord(mealRecord);
        }

        final MealRecordDomain mealRecordCreated = mealRecordGateway.insert(mealRecord);

        log.info(
                LogField.Placeholders.EIGHT.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Successfully inserted new meal record"),
                StructuredArguments.kv(LogField.MEAL_RECORD_ID.label(), mealRecordCreated.id()),
                StructuredArguments.kv(LogField.MEAL_RECORD_CREATED_AT.label(), mealRecordCreated.createdAt()),
                StructuredArguments.kv(LogField.MEAL_RECORD_CONSUMED_AT.label(), mealRecordCreated.consumedAt()),
                StructuredArguments.kv(
                        LogField.MEAL_RECORD_MEAL_TYPE_ID.label(),
                        mealRecordCreated.mealType().id()),
                StructuredArguments.kv(LogField.MEAL_RECORD_IS_FREE_MEAL.label(), mealRecordCreated.isFreeMeal()),
                StructuredArguments.kv(
                        LogField.MEAL_RECORD_STANDARD_OPTION_ID.label(),
                        mealRecordCreated
                                .getStandardOption()
                                .map(StandardOptionDomain::id)
                                .orElse(null)),
                StructuredArguments.kv(
                        LogField.MEAL_RECORD_FREE_MEAL_DESCRIPTION.label(), mealRecordCreated.freeMealDescription()));

        return mealRecordCreated;
    }

    public List<MealRecordDomain> searchByConsumedAt(
            final LocalDateTime consumedAtAfter, final LocalDateTime consumedAtBefore) {
        log.info(
                LogField.Placeholders.THREE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Starting to search meal record by consumedAt"),
                StructuredArguments.kv(LogField.CONSUMED_AT_AFTER.label(), consumedAtAfter),
                StructuredArguments.kv(LogField.CONSUMED_AT_BEFORE.label(), consumedAtBefore));

        final List<MealRecordDomain> mealRecords =
                mealRecordGateway.searchByConsumedAt(consumedAtAfter, consumedAtBefore);

        log.info(
                LogField.Placeholders.FOUR.getPlaceholder(),
                StructuredArguments.kv(
                        LogField.MSG.label(), "Successfully finished searching meal record by consumedAt"),
                StructuredArguments.kv(LogField.CONSUMED_AT_AFTER.label(), consumedAtAfter),
                StructuredArguments.kv(LogField.CONSUMED_AT_BEFORE.label(), consumedAtBefore),
                StructuredArguments.kv(LogField.MEAL_RECORD_SIZE.label(), mealRecords.size()));

        return mealRecords;
    }

    public PageResultDomain<MealRecordDomain> search(final SearchMealRecordParamDomain searchParam) {
        log.info(
                LogField.Placeholders.SIX.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Starting to search meal record"),
                StructuredArguments.kv(LogField.MEAL_RECORD_SEARCH_PAGE_NUMBER.label(), searchParam.getPageNumber()),
                StructuredArguments.kv(LogField.MEAL_RECORD_SEARCH_PAGE_SIZE.label(), searchParam.getPageSize()),
                StructuredArguments.kv(
                        LogField.MEAL_RECORD_SEARCH_CONSUMED_AT_AFTER.label(), searchParam.getConsumedAtAfter()),
                StructuredArguments.kv(
                        LogField.MEAL_RECORD_SEARCH_CONSUMED_AT_BEFORE.label(), searchParam.getConsumedAtBefore()),
                StructuredArguments.kv(LogField.MEAL_RECORD_SEARCH_IS_FREE_MEAL.label(), searchParam.getIsFreeMeal()));

        final PageResultDomain<MealRecordDomain> searchResult = mealRecordGateway.search(searchParam);

        log.info(
                LogField.Placeholders.SIX.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Successfully finished searching meal record"),
                StructuredArguments.kv(LogField.MEAL_RECORD_SEARCH_TOTAL_PAGES.label(), searchResult.totalPages()),
                StructuredArguments.kv(LogField.MEAL_RECORD_SEARCH_CURRENT_PAGE.label(), searchResult.currentPage()),
                StructuredArguments.kv(LogField.MEAL_RECORD_SEARCH_PAGE_SIZE.label(), searchResult.pageSize()),
                StructuredArguments.kv(LogField.MEAL_RECORD_SEARCH_TOTAL_ITEMS.label(), searchResult.totalItems()),
                StructuredArguments.kv(
                        LogField.MEAL_RECORD_SEARCH_CONTENT_SIZE.label(),
                        searchResult.content().size()));

        return searchResult;
    }

    private static void validateFreeMealRecord(final MealRecordDomain mealRecordDomain) {
        if (mealRecordDomain.standardOption() != null) {
            log.warn(
                    LogField.Placeholders.FIVE.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Free meal record cannot have standard options"),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_MEAL_TYPE_ID.label(),
                            mealRecordDomain.mealType().id()),
                    StructuredArguments.kv(LogField.MEAL_RECORD_IS_FREE_MEAL.label(), mealRecordDomain.isFreeMeal()),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_STANDARD_OPTION_ID.label(),
                            mealRecordDomain.standardOption().id()),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_FREE_MEAL_DESCRIPTION.label(),
                            mealRecordDomain.freeMealDescription()));

            throw new InvalidMealRecordException("Free meal record cannot have standard options");
        }

        if (mealRecordDomain.freeMealDescription() == null
                || mealRecordDomain.freeMealDescription().isBlank()) {
            log.warn(
                    LogField.Placeholders.FIVE.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Free meal record has invalid meal description"),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_MEAL_TYPE_ID.label(),
                            mealRecordDomain.mealType().id()),
                    StructuredArguments.kv(LogField.MEAL_RECORD_IS_FREE_MEAL.label(), mealRecordDomain.isFreeMeal()),
                    StructuredArguments.kv(LogField.MEAL_RECORD_STANDARD_OPTION_ID.label(), null),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_FREE_MEAL_DESCRIPTION.label(),
                            mealRecordDomain.freeMealDescription()));

            throw new InvalidMealRecordException("Free meal record has invalid meal description");
        }
    }

    private void validatePlannedMealRecord(final MealRecordDomain mealRecordDomain) {
        if (mealRecordDomain.freeMealDescription() != null) {
            final Long standardOptionId = Optional.ofNullable(mealRecordDomain.standardOption())
                    .map(StandardOptionDomain::id)
                    .orElse(null);
            log.warn(
                    LogField.Placeholders.FIVE.getPlaceholder(),
                    StructuredArguments.kv(
                            LogField.MSG.label(), "Planned meal record cannot have free meal description"),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_MEAL_TYPE_ID.label(),
                            mealRecordDomain.mealType().id()),
                    StructuredArguments.kv(LogField.MEAL_RECORD_IS_FREE_MEAL.label(), mealRecordDomain.isFreeMeal()),
                    StructuredArguments.kv(LogField.MEAL_RECORD_STANDARD_OPTION_ID.label(), standardOptionId),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_FREE_MEAL_DESCRIPTION.label(),
                            mealRecordDomain.freeMealDescription()));

            throw new InvalidMealRecordException("Planned meal record cannot have free meal description");
        }

        if (mealRecordDomain.standardOption() == null
                || mealRecordDomain.standardOption().id() == null) {
            log.warn(
                    LogField.Placeholders.FIVE.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Planned meal record has invalid standard options"),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_MEAL_TYPE_ID.label(),
                            mealRecordDomain.mealType().id()),
                    StructuredArguments.kv(LogField.MEAL_RECORD_IS_FREE_MEAL.label(), mealRecordDomain.isFreeMeal()),
                    StructuredArguments.kv(LogField.MEAL_RECORD_STANDARD_OPTION_ID.label(), null),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_FREE_MEAL_DESCRIPTION.label(),
                            mealRecordDomain.freeMealDescription()));

            throw new InvalidMealRecordException("Planned meal record has invalid standard options");
        }

        if (!standardOptionGateway.existsByIdAndDietPlanActive(
                mealRecordDomain.standardOption().id(),
                mealRecordDomain.mealType().id())) {
            log.warn(
                    LogField.Placeholders.FIVE.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "StandardOption was not found in given Meal Type"),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_MEAL_TYPE_ID.label(),
                            mealRecordDomain.mealType().id()),
                    StructuredArguments.kv(LogField.MEAL_RECORD_IS_FREE_MEAL.label(), mealRecordDomain.isFreeMeal()),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_STANDARD_OPTION_ID.label(),
                            mealRecordDomain.standardOption().id()),
                    StructuredArguments.kv(
                            LogField.MEAL_RECORD_FREE_MEAL_DESCRIPTION.label(),
                            mealRecordDomain.freeMealDescription()));

            final String errMsg = """
                Standard Option was not found in given Meal Type | MealType ID informed: %s - StandardOption ID informed: %s
                """.formatted(
                            mealRecordDomain.mealType().id(),
                            mealRecordDomain.standardOption().id());

            throw new StandardOptionNotFoundException(errMsg);
        }
    }
}
