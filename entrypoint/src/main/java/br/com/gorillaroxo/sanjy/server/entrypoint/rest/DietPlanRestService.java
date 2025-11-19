package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateDietPlanRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Diet Plan", description = "Handles diet plan operations")
public interface DietPlanRestService {

    @Operation(
            summary = "Get the currently active diet plan",
            description =
                    "Retrieves the currently active diet plan with all meal types, standard options, nutritional targets (calories, protein, carbs, fat), and goals. "
                            + "Only one diet plan can be active at a time. Use this to check the current diet configuration.")
    DietPlanCompleteResponseDTO activeDietPlan();

    @Operation(
            summary = "Create a new diet plan",
            description =
                    "Creates a new diet plan with meal types (breakfast, lunch, snack, dinner, etc.), standard meal options, nutritional targets (daily calories, protein, carbs, fat), and goals. "
                            + "The new plan is automatically set as active and any previously active plan is deactivated. Each meal type can have multiple standard options for variety.")
    DietPlanCompleteResponseDTO newDietPlan(@RequestBody @Valid @NotNull CreateDietPlanRequestDto request);
}
