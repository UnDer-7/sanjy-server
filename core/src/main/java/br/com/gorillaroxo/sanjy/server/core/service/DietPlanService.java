package br.com.gorillaroxo.sanjy.server.core.service;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.core.exception.StandardOptionsNotInSequence;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.DietPlanGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DietPlanService {

    private final DietPlanGateway dietPlanGateway;

    public DietPlanDomain insert(final DietPlanDomain newDietPlan) {
        validateMealTypeStandardOptions(newDietPlan);

        dietPlanGateway.findActive()
            .ifPresent(dietPlanFound -> {
                dietPlanFound.deactivate();
                dietPlanGateway.insert(dietPlanFound);
            });

        newDietPlan.activate();

        return dietPlanGateway.insert(newDietPlan);
    }

    private void validateMealTypeStandardOptions(final DietPlanDomain dietPlan) {
        dietPlan.getMealTypes()
            .forEach(mealType -> {
                final List<Long> standardOptionNumbersSorted = mealType.standardOptions()
                    .stream()
                    .map(StandardOptionDomain::optionNumber)
                    .sorted(Comparator.comparing(Function.identity()))
                    .toList();

                final Long firstOptionNumber = standardOptionNumbersSorted.getFirst();
                if (firstOptionNumber != 1) {
                    log.warn(
                        LogField.Placeholders.FIVE.placeholder,
                        StructuredArguments.kv(LogField.MSG.label(), "StandardOptions informed does not start with number 1"),
                        StructuredArguments.kv(LogField.STANDARD_OPTIONS_OPTION_NUMBER.label(), firstOptionNumber),
                        StructuredArguments.kv(LogField.STANDARD_OPTIONS_SIZE.label(), standardOptionNumbersSorted.size()),
                        StructuredArguments.kv(LogField.MEAL_TYPE_NAME.label(), mealType.name()),
                        StructuredArguments.kv(LogField.MEAL_TYPE_ID.label(), mealType.id()));

                    throw new StandardOptionsNotInSequence(
                        "StandardOptions must start with number 1, but started with number %d | MealType '%s' (ID: %s)".formatted(
                            firstOptionNumber, mealType.name(), mealType.id()));
                }

                if (standardOptionNumbersSorted.size() > 1) {
                    for (int i = 1; i < standardOptionNumbersSorted.size(); i++) {

                        if (!Objects.equals(standardOptionNumbersSorted.get(i), standardOptionNumbersSorted.get(i - 1) + 1L)) {

                            final String optionsNumbersListStr = standardOptionNumbersSorted.stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(", "));

                            log.warn(
                                LogField.Placeholders.FIVE.placeholder,
                                StructuredArguments.kv(LogField.MSG.label(), "StandardOptions informed are not sequential"),
                                StructuredArguments.kv(LogField.STANDARD_OPTIONS_LIST.label(), optionsNumbersListStr),
                                StructuredArguments.kv(LogField.STANDARD_OPTIONS_SIZE.label(), standardOptionNumbersSorted.size()),
                                StructuredArguments.kv(LogField.MEAL_TYPE_NAME.label(), mealType.name()),
                                StructuredArguments.kv(LogField.MEAL_TYPE_ID.label(), mealType.id()));

                            throw new StandardOptionsNotInSequence(
                                "MealType '%s' (ID: %s) has StandardOptions with non-sequential numbers (skipped or repeated numbers detected)".formatted(
                                    mealType.name(), mealType.id()));
                        }
                    }
                }
            });
    }

}
