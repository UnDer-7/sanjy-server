package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.CreateDietPlanFileUseCase;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.rest.NewDietPlanFileRestService;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.DietPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class NewDietPlanFileController implements NewDietPlanFileRestService {

    private final CreateDietPlanFileUseCase createDietPlanFileUseCase;
    private final DietPlanMapper dietPlanMapper;

    @Override
    @PostMapping("/v1/diet-plan/file")
    public DietPlanCompleteResponseDTO newDietPlan(@RequestParam("file") MultipartFile file) {
        final DietPlanDomain dietPlan = createDietPlanFileUseCase.execute(file);
        return dietPlanMapper.toDTO(dietPlan);
    }

}
