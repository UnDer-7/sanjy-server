package br.com.gorillaroxo.sanjy.server.core.service;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.core.exception.RepeatedMealTypeNamesException;
import br.com.gorillaroxo.sanjy.server.core.exception.StandardOptionsNotInSequence;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.DietPlanGateway;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DietPlanService {

    private final DietPlanGateway dietPlanGateway;

    public DietPlanDomain insert(final DietPlanDomain newDietPlan) {
        log.info(
                LogField.Placeholders.THREE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "Starting to insert new diet plan"),
                StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), newDietPlan.getName()),
                StructuredArguments.kv(
                        LogField.DIET_PLAN_MEAL_TYPE_SIZE.label(),
                        newDietPlan.getMealTypes().size()));

        validateMealTypeStandardOptions(newDietPlan);
        validateMealType(newDietPlan);

        dietPlanGateway.findActive().ifPresent(dietPlanFound -> {
            log.info(
                    LogField.Placeholders.SIX.placeholder,
                    StructuredArguments.kv(LogField.MSG.label(), "Starting to deactivate old diet plan"),
                    StructuredArguments.kv(LogField.DIET_PLAN_ID.label(), dietPlanFound.getId()),
                    StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), dietPlanFound.getName()),
                    StructuredArguments.kv(LogField.DIET_PLAN_IS_ACTIVE.label(), dietPlanFound.getIsActive()),
                    StructuredArguments.kv(LogField.DIET_PLAN_CREATED_AT.label(), dietPlanFound.getCreatedAt()),
                    StructuredArguments.kv(
                            LogField.DIET_PLAN_MEAL_TYPE_SIZE.label(),
                            dietPlanFound.getMealTypes().size()));

            dietPlanFound.deactivate();
            dietPlanGateway.insert(dietPlanFound);

            log.info(
                    LogField.Placeholders.SIX.placeholder,
                    StructuredArguments.kv(LogField.MSG.label(), "Successfully deactivated old diet plan"),
                    StructuredArguments.kv(LogField.DIET_PLAN_ID.label(), dietPlanFound.getId()),
                    StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), dietPlanFound.getName()),
                    StructuredArguments.kv(LogField.DIET_PLAN_IS_ACTIVE.label(), dietPlanFound.getIsActive()),
                    StructuredArguments.kv(LogField.DIET_PLAN_CREATED_AT.label(), dietPlanFound.getCreatedAt()),
                    StructuredArguments.kv(
                            LogField.DIET_PLAN_MEAL_TYPE_SIZE.label(),
                            dietPlanFound.getMealTypes().size()));
        });

        newDietPlan.activate();

        final DietPlanDomain dietPlanCreated = dietPlanGateway.insert(newDietPlan);

        log.info(
                LogField.Placeholders.SIX.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "Successfully inserted new diet pla"),
                StructuredArguments.kv(LogField.DIET_PLAN_ID.label(), dietPlanCreated.getId()),
                StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), dietPlanCreated.getName()),
                StructuredArguments.kv(LogField.DIET_PLAN_IS_ACTIVE.label(), dietPlanCreated.getIsActive()),
                StructuredArguments.kv(LogField.DIET_PLAN_CREATED_AT.label(), dietPlanCreated.getCreatedAt()),
                StructuredArguments.kv(
                        LogField.DIET_PLAN_MEAL_TYPE_SIZE.label(),
                        dietPlanCreated.getMealTypes().size()));

        return dietPlanCreated;
    }

    private static void validateMealType(final DietPlanDomain newDietPlan) {
        final List<String> repeatedMealTypeNames = newDietPlan.getMealTypes().stream()
                .collect(Collectors.groupingBy(mt -> mt.name().trim().toLowerCase(), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();
        if (!repeatedMealTypeNames.isEmpty()) {
            final var repeatedMealTypeNamesStr = String.join(", ", repeatedMealTypeNames);

            log.warn(
                    LogField.Placeholders.FOUR.placeholder,
                    StructuredArguments.kv(LogField.MSG.label(), "Diet plan has repeated meal type names"),
                    StructuredArguments.kv(LogField.MEAL_TYPE_NAME.label(), "( " + repeatedMealTypeNamesStr + " ) "),
                    StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), newDietPlan.getName()),
                    StructuredArguments.kv(
                            LogField.DIET_PLAN_MEAL_TYPE_SIZE.label(),
                            newDietPlan.getMealTypes().size()));

            throw new RepeatedMealTypeNamesException("Repeated meal type names: " + repeatedMealTypeNamesStr);
        }
    }

    private void validateMealTypeStandardOptions(final DietPlanDomain dietPlan) {
        dietPlan.getMealTypes().forEach(mealType -> {
            final List<Long> standardOptionNumbersSorted = mealType.standardOptions().stream()
                    .map(StandardOptionDomain::optionNumber)
                    .sorted(Comparator.comparing(Function.identity()))
                    .toList();

            final Long firstOptionNumber = standardOptionNumbersSorted.getFirst();
            if (firstOptionNumber != 1) {
                log.warn(
                        LogField.Placeholders.SEVEN.placeholder,
                        StructuredArguments.kv(
                                LogField.MSG.label(), "StandardOptions informed does not start with number 1"),
                        StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), dietPlan.getName()),
                        StructuredArguments.kv(
                                LogField.DIET_PLAN_MEAL_TYPE_SIZE.label(),
                                dietPlan.getMealTypes().size()),
                        StructuredArguments.kv(LogField.STANDARD_OPTIONS_OPTION_NUMBER.label(), firstOptionNumber),
                        StructuredArguments.kv(
                                LogField.STANDARD_OPTIONS_SIZE.label(), standardOptionNumbersSorted.size()),
                        StructuredArguments.kv(LogField.MEAL_TYPE_NAME.label(), mealType.name()),
                        StructuredArguments.kv(LogField.MEAL_TYPE_ID.label(), mealType.id()));

                throw new StandardOptionsNotInSequence(
                        "StandardOptions must start with number 1, but started with number %d | MealType '%s' (ID: %s)"
                                .formatted(firstOptionNumber, mealType.name(), mealType.id()));
            }

            if (standardOptionNumbersSorted.size() > 1) {
                for (int i = 1; i < standardOptionNumbersSorted.size(); i++) {

                    if (!Objects.equals(
                            standardOptionNumbersSorted.get(i), standardOptionNumbersSorted.get(i - 1) + 1L)) {

                        final String optionsNumbersListStr = standardOptionNumbersSorted.stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(", "));

                        log.warn(
                                LogField.Placeholders.SEVEN.placeholder,
                                StructuredArguments.kv(
                                        LogField.MSG.label(), "StandardOptions informed are not sequential"),
                                StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), dietPlan.getName()),
                                StructuredArguments.kv(
                                        LogField.DIET_PLAN_MEAL_TYPE_SIZE.label(),
                                        dietPlan.getMealTypes().size()),
                                StructuredArguments.kv(LogField.STANDARD_OPTIONS_LIST.label(), optionsNumbersListStr),
                                StructuredArguments.kv(
                                        LogField.STANDARD_OPTIONS_SIZE.label(), standardOptionNumbersSorted.size()),
                                StructuredArguments.kv(LogField.MEAL_TYPE_NAME.label(), mealType.name()),
                                StructuredArguments.kv(LogField.MEAL_TYPE_ID.label(), mealType.id()));

                        throw new StandardOptionsNotInSequence(
                                "MealType '%s' (ID: %s) has StandardOptions with non-sequential numbers (skipped or repeated numbers detected)"
                                        .formatted(mealType.name(), mealType.id()));
                    }
                }
            }
        });
    }
}
