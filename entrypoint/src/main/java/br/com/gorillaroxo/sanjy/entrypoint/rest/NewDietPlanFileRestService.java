package br.com.gorillaroxo.sanjy.entrypoint.rest;

import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.DietPlanCompleteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Diet Plan")
public interface NewDietPlanFileRestService {

    @Operation(
        summary = "Upload diet plan PDF file"
    )
    DietPlanCompleteResponseDTO newDietPlan(@RequestParam("file") MultipartFile file);
}
