package br.com.gorillaroxo.sanjy.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.core.ports.driver.AvailableMealTypesUseCase;
import br.com.gorillaroxo.sanjy.core.ports.driver.RegisterMealRecordUseCase;
import br.com.gorillaroxo.sanjy.entrypoint.dto.request.CreateMealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.DietPlanCompleteResponseDTO;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.entrypoint.rest.MealRecordRestService;
import br.com.gorillaroxo.sanjy.infrastructure.mapper.DietPlanMapper;
import br.com.gorillaroxo.sanjy.infrastructure.mapper.MealRecordMapper;
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
public class MealRecordController implements MealRecordRestService {

    private final AvailableMealTypesUseCase availableMealTypesUseCase;
    private final RegisterMealRecordUseCase registerMealRecordUseCase;
    private final MealRecordMapper mealRecordMapper;
    private final DietPlanMapper dietPlanMapper;

    @Override
    @PostMapping("/v1/meal-record")
    @ResponseStatus(HttpStatus.CREATED)
    @Tool(name = "newMealRecord", description = "Registers a new meal record")
    public MealRecordResponseDTO newMealRecord(final CreateMealRecordRequestDTO request) {
        log.info("Registering a new meal record...");
        final var mealRecord = mealRecordMapper.toDomain(request);
        final MealRecordDomain mealRecordSaved = registerMealRecordUseCase.execute(mealRecord);
        return mealRecordMapper.toDTO(mealRecordSaved);
    }

    @Override
    @GetMapping("/v1/meal-record")
    @Tool(name = "activeDietPlan", description = "Retrieves the active diet plan for the user")
    public DietPlanCompleteResponseDTO activeDietPlan() {
        log.info("Retrieving active diet plan for the user...");
        final DietPlanDomain dietPlan = availableMealTypesUseCase.execute();

        return dietPlanMapper.toDTO(dietPlan);
    }

}
