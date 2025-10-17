package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
    name = "Meal Record",
    description = "Handles meal record operations"
)
public interface MealRecordRestService {


    MealRecordResponseDTO newMealRecord(@RequestBody @Valid @NotNull CreateMealRecordRequestDTO request);

    List<MealRecordResponseDTO> getTodayMealRecords();

}
