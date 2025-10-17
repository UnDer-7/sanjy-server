package br.com.gorillaroxo.sanjy.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.core.ports.driver.AvailableMealTypesUseCase;
import br.com.gorillaroxo.sanjy.core.ports.driver.CreateDietPlanUseCase;
import br.com.gorillaroxo.sanjy.entrypoint.dto.request.CreateDietPlanRequestDTO;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.DietPlanCompleteResponseDTO;
import br.com.gorillaroxo.sanjy.entrypoint.rest.DietPlanRestService;
import br.com.gorillaroxo.sanjy.infrastructure.chat.tool.SanjyAgentTool;
import br.com.gorillaroxo.sanjy.infrastructure.mapper.DietPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class DietPlanController implements DietPlanRestService, SanjyAgentTool {

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
    public DietPlanCompleteResponseDTO newDietPlan(final CreateDietPlanRequestDTO request) {
        final DietPlanDomain dietPlan = dietPlanMapper.toDomain(request);
        return dietPlanMapper.toDTO(createDietPlanUseCase.execute(dietPlan));
    }

}
