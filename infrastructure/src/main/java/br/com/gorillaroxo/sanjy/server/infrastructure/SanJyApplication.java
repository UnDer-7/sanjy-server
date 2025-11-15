package br.com.gorillaroxo.sanjy.server.infrastructure;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateDietPlanRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealTypesRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateStandardOptionRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.PageRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.SearchMealRecordParamRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDTO;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.projection.MealRecordStatisticsProjection;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "br.com.gorillaroxo.sanjy")
@EntityScan(basePackages = "br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity")
@ConfigurationPropertiesScan(basePackages = "br.com.gorillaroxo.sanjy.server.infrastructure.config")
@RegisterReflectionForBinding({
    MealRecordStatisticsProjection.class,
    CreateDietPlanRequestDTO.class,
    CreateMealRecordRequestDTO.class,
    CreateMealTypesRequestDTO.class,
    CreateStandardOptionRequestDTO.class,
    PageRequestDTO.class,
    SearchMealRecordParamRequestDTO.class,
    ErrorResponseDTO.class
})
public class SanJyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SanJyApplication.class, args);
    }

}
