package br.com.gorillaroxo.sanjy.entrypoint.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(
    name = "Diet Plan",
    description = "Handles diet plan operations"
)
public interface DietPlanRestService {

    @Operation(
        summary = "Upload diet plan PDF file"
    )
    @PostMapping("/v1/diet-plan")
    void newDietPlan(@RequestParam("file") MultipartFile file);

}
