package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.domain.PatchableDietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.exception.DietPlanNotFoundException;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.DietPlanGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatchDietPlanUseCase {

    private final DietPlanGateway dietPlanGateway;

    public DietPlanDomain execute(final PatchableDietPlanDomain patchableDietPlan) {
        final DietPlanDomain foundDietPlan = dietPlanGateway
                .findById(patchableDietPlan.getId())
                .orElseThrow(() ->
                        new DietPlanNotFoundException("Could not find diet plan with id " + patchableDietPlan.getId()));

        log.info(
            LogField.Placeholders.FOUR.getPlaceholder(),
            StructuredArguments.kv(LogField.MSG.label(), "Diet Plan before patching"),
            StructuredArguments.kv(LogField.DIET_PLAN_ID.label(), foundDietPlan.getId()),
            StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), foundDietPlan.getName()),
            StructuredArguments.kv(LogField.DIET_PLAN.label(), foundDietPlan.toPatchableFieldsString()));

        foundDietPlan.patch(patchableDietPlan);
        final DietPlanDomain updated = dietPlanGateway.patch(foundDietPlan);

        log.info(
            LogField.Placeholders.FOUR.getPlaceholder(),
            StructuredArguments.kv(LogField.MSG.label(), "Diet Plan after patching"),
            StructuredArguments.kv(LogField.DIET_PLAN_ID.label(), updated.getId()),
            StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), updated.getName()),
            StructuredArguments.kv(LogField.DIET_PLAN.label(), updated.toPatchableFieldsString()));

        return updated;
    }
}
