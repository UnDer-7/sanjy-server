package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.PatchableDietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.exception.DietPlanNotFoundException;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.DietPlanGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        foundDietPlan.patch(patchableDietPlan);
        return dietPlanGateway.patch(foundDietPlan);
    }
}
