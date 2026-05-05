package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.domain.MealTypeDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.PatchableMealTypeDomain;
import br.com.gorillaroxo.sanjy.server.core.exception.MealTypeDuplicateNameException;
import br.com.gorillaroxo.sanjy.server.core.exception.MealTypeNotFoundException;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.MealTypeGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatchMealTypeUseCase {

    private final MealTypeGateway mealTypeGateway;

    public MealTypeDomain execute(final PatchableMealTypeDomain patchable) {
        final MealTypeDomain mealType = mealTypeGateway
                .findById(patchable.getId())
                .orElseThrow(() -> new MealTypeNotFoundException("Could not find meal type with id " + patchable.getId()));

        log.info(
                LogField.Placeholders.THREE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Meal Type before patching"),
                StructuredArguments.kv(LogField.MEAL_TYPE_ID.label(), mealType.id()),
                StructuredArguments.kv(LogField.MEAL_TYPE_NAME.label(), mealType.name()));

        patchable.getName().ifPresent(name -> {
            if (mealTypeGateway.existsByDietPlanIdAndNameAndIdNot(mealType.dietPlanId(), name, mealType.id())) {
                throw new MealTypeDuplicateNameException(
                        "Meal type with name '" + name + "' already exists in diet plan with id " + mealType.dietPlanId());
            }
        });

        final MealTypeDomain patched = mealType.patch(patchable);
        final MealTypeDomain saved = mealTypeGateway.patch(patched);

        log.info(
                LogField.Placeholders.THREE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Meal Type after patching"),
                StructuredArguments.kv(LogField.MEAL_TYPE_ID.label(), saved.id()),
                StructuredArguments.kv(LogField.MEAL_TYPE_NAME.label(), saved.name()));

        return saved;
    }
}
