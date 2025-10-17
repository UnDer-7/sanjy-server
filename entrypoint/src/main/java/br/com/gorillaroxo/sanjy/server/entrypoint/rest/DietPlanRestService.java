package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateDietPlanRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
    name = "Diet Plan",
    description = "Handles diet plan operations"
)
public interface DietPlanRestService {

    DietPlanCompleteResponseDTO activeDietPlan();

    DietPlanCompleteResponseDTO newDietPlan(@RequestBody CreateDietPlanRequestDTO request);
}
