package br.com.gorillaroxo.sanjy.core.ports.driver;

import org.springframework.web.multipart.MultipartFile;

public interface ProcessDietPlanFileUseCase {

    void execute(final MultipartFile file);

}
