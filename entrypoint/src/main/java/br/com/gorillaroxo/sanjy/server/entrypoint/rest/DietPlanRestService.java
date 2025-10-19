package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateDietPlanRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
    name = "Diet Plan",
    description = "Handles diet plan operations"
)
public interface DietPlanRestService {

    @Operation(
        summary = "Get the currently active diet plan",
        description = "Retrieves the complete details of the currently active diet plan, including all meal types and their standard options. " +
                      "Only one diet plan can be active at any given time. Returns the plan with nutritional targets, goals, and all configured meals."
    )
    DietPlanCompleteResponseDTO activeDietPlan();

    @Operation(
        summary = "Create a new diet plan",
        description = "Creates a new diet plan with meal types and standard meal options. " +
                      "The diet plan includes nutritional targets (daily calories, protein, carbs, fat), goals, and meal configurations. " +
                      "When a new plan is created and set as active, any previously active plan will be automatically deactivated."
    )
    DietPlanCompleteResponseDTO newDietPlan(@RequestBody @Valid @NotNull CreateDietPlanRequestDTO request);
}
