package br.com.gorillaroxo.sanjy.entrypoint.rest;

import br.com.gorillaroxo.sanjy.entrypoint.dto.request.CreateMealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.DietPlanCompleteResponseDTO;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.MealTypeResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
    name = "Meal Record",
    description = "Handles meal record operations"
)
public interface MealRecordRestService {


    MealRecordResponseDTO newMealRecord(@RequestBody @Valid @NotNull CreateMealRecordRequestDTO request);


    DietPlanCompleteResponseDTO activeDietPlan();
}
