package br.com.gorillaroxo.sanjy.core.service;

import br.com.gorillaroxo.sanjy.core.domain.plan.DietPlanDomain;
import br.com.gorillaroxo.sanjy.core.ports.driven.DietPlanGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DietPlanService {

    private final DietPlanGateway dietPlanGateway;

    public DietPlanDomain insert(final DietPlanDomain newDietPlan) {
        Objects.requireNonNull(newDietPlan);

        dietPlanGateway.findActive()
            .ifPresent(dietPlanFound -> {
                dietPlanFound.deactivate();
                dietPlanGateway.insert(dietPlanFound);
            });

        newDietPlan.activate();

        return dietPlanGateway.insert(newDietPlan);
    }
}
