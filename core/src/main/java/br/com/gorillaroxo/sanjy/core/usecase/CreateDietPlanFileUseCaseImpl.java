package br.com.gorillaroxo.sanjy.core.usecase;

import br.com.gorillaroxo.sanjy.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.core.ports.driven.DietPlanConverterAgentGateway;
import br.com.gorillaroxo.sanjy.core.ports.driven.DietPlanVectorStoreGateway;
import br.com.gorillaroxo.sanjy.core.ports.driver.CreateDietPlanFileUseCase;
import br.com.gorillaroxo.sanjy.core.service.DietPlanService;
import br.com.gorillaroxo.sanjy.core.service.ExtractTextFromFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateDietPlanFileUseCaseImpl implements CreateDietPlanFileUseCase {

    private final ExtractTextFromFileService extractTextFromFileService;
    private final DietPlanVectorStoreGateway dietPlanVectorStoreGateway;
    private final DietPlanConverterAgentGateway dietPlanConverterAgentGateway;
    private final DietPlanService dietPlanService;

    // todo: Receber outro Objeto para nao ter o Spring Web no core
    public DietPlanDomain execute(final MultipartFile file) {
        final String fileTxt = extractTextFromFileService.execute(file);
        final DietPlanDomain dietPlan = dietPlanConverterAgentGateway.convert(fileTxt);
        return dietPlanService.insert(dietPlan);
    }

}
