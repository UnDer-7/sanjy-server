package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.CreateDietPlanUseCase;
import br.com.gorillaroxo.sanjy.server.core.service.DietPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class CreateDietPlanUseCaseImpl implements CreateDietPlanUseCase {

    private final DietPlanService dietPlanService;

    @Override
    public DietPlanDomain execute(final DietPlanDomain dietPlan) {
        return dietPlanService.insert(dietPlan);
    }
}
