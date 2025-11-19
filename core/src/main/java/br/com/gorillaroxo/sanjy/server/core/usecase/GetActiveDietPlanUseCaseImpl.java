package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.exception.DietPlanNotFoundException;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.DietPlanGateway;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetActiveDietPlanUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetActiveDietPlanUseCaseImpl implements GetActiveDietPlanUseCase {

    private final DietPlanGateway dietPlanGateway;

    @Override
    public DietPlanDomain execute() {
        log.info(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Starting to get active diet plan"));

        final DietPlanDomain dietPlan = dietPlanGateway.findActive().orElseThrow(() -> {
            log.warn(
                    LogField.Placeholders.ONE.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Could not find active diet plan"));
            return new DietPlanNotFoundException("Could not find active diet plan");
        });

        log.info(
                LogField.Placeholders.SIX.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Successfully found active diet plan"),
                StructuredArguments.kv(LogField.DIET_PLAN_ID.label(), dietPlan.getId()),
                StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), dietPlan.getName()),
                StructuredArguments.kv(LogField.DIET_PLAN_IS_ACTIVE.label(), dietPlan.getIsActive()),
                StructuredArguments.kv(LogField.DIET_PLAN_CREATED_AT.label(), dietPlan.getCreatedAt()),
                StructuredArguments.kv(
                        LogField.DIET_PLAN_MEAL_TYPE_SIZE.label(),
                        dietPlan.getMealTypes().size()));

        return dietPlan;
    }
}
