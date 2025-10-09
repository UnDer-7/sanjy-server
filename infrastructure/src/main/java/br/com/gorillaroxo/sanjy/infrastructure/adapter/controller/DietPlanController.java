package br.com.gorillaroxo.sanjy.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.core.ports.driver.ProcessDietPlanFileUseCase;
import br.com.gorillaroxo.sanjy.entrypoint.rest.DietPlanRestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class DietPlanController implements DietPlanRestService {

    private final ProcessDietPlanFileUseCase processDietPlanFileUseCase;

    @Override
    public void uploadPdf(@RequestParam("file") MultipartFile file) {
        processDietPlanFileUseCase.execute(file);
    }

}
