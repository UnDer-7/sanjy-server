package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.DietPlanGateway;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.AvailableMealTypesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvailableMealTypesUseCaseImpl implements AvailableMealTypesUseCase {

    private final DietPlanGateway dietPlanGateway;

    @Override
    public DietPlanDomain execute() {
        return dietPlanGateway.findActive()
            // todo: jogar exception
            .orElseThrow();
    }

}
