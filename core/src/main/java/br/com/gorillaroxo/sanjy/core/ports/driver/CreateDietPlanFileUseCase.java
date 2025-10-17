package br.com.gorillaroxo.sanjy.core.ports.driver;

import br.com.gorillaroxo.sanjy.core.domain.DietPlanDomain;
import org.springframework.web.multipart.MultipartFile;

public interface CreateDietPlanFileUseCase {

    DietPlanDomain execute(final MultipartFile file);

}
