package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.AvailableMealTypesUseCase;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.CreateDietPlanUseCase;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateDietPlanRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.rest.DietPlanRestService;
import br.com.gorillaroxo.sanjy.server.infrastructure.config.McpToolMarker;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.DietPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class DietPlanController implements DietPlanRestService, McpToolMarker {

    private final CreateDietPlanUseCase createDietPlanUseCase;
    private final AvailableMealTypesUseCase availableMealTypesUseCase;
    private final DietPlanMapper dietPlanMapper;

    @Override
    @GetMapping("/v1/diet-plan/active")
    @Tool(name = "activeDietPlan", description = "Retrieves the active diet plan for the user")
    public DietPlanCompleteResponseDTO activeDietPlan() {
        log.info("Retrieving active diet plan for the user...");
        final DietPlanDomain dietPlan = availableMealTypesUseCase.execute();

        return dietPlanMapper.toDTO(dietPlan);
    }

    @Override
    @PostMapping("/v1/diet-plan")
    @ResponseStatus(HttpStatus.CREATED)
    @Tool(name = "newDietPlan", description = "Creates a new diet plan for the user")
    public DietPlanCompleteResponseDTO newDietPlan(final CreateDietPlanRequestDTO request) {
        final DietPlanDomain dietPlan = dietPlanMapper.toDomain(request);
        return dietPlanMapper.toDTO(createDietPlanUseCase.execute(dietPlan));
    }

}
