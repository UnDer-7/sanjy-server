package br.com.gorillaroxo.sanjy.entrypoint.rest;

import br.com.gorillaroxo.sanjy.entrypoint.dto.request.CreateDietPlanRequestDTO;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.DietPlanCompleteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(
    name = "Diet Plan",
    description = "Handles diet plan operations"
)
public interface DietPlanRestService {

    DietPlanCompleteResponseDTO activeDietPlan();

    DietPlanCompleteResponseDTO newDietPlan(@RequestBody CreateDietPlanRequestDTO request);
}
