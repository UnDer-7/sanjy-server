package br.com.gorillaroxo.sanjy.core.service;

import br.com.gorillaroxo.sanjy.core.domain.DietPlanDomain;
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
        // Todo: validar dados
        // todo: validar ordem dos standardOptions, se na tipo: option 2 e 5
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
