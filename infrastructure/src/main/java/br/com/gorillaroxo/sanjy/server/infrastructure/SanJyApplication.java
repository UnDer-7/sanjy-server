package br.com.gorillaroxo.sanjy.server.infrastructure;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateDietPlanRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealRecordRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealTypesRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateStandardOptionRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.PageRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.SearchMealRecordParamRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.IdOnlyResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordCreatedResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealTypeSimplifiedResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.PageResponseMealRecordDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ProjectInfoResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.StandardOptionSimplifiedResponseDto;
import br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.github.dto.response.GitHubReleaseResponseDto;
import br.com.gorillaroxo.sanjy.server.infrastructure.config.TimezoneInitializer;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.projection.MealRecordStatisticsProjection;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "br.com.gorillaroxo.sanjy")
@EntityScan(basePackages = "br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity")
@EnableFeignClients(basePackages = "br.com.gorillaroxo.sanjy.server.infrastructure.client.rest")
@ImportAutoConfiguration(FeignAutoConfiguration.class)
@ConfigurationPropertiesScan(basePackages = "br.com.gorillaroxo.sanjy.server.infrastructure.config")
@RegisterReflectionForBinding({
    MealRecordStatisticsProjection.class,
    CreateDietPlanRequestDto.class,
    CreateMealRecordRequestDto.class,
    CreateMealTypesRequestDto.class,
    CreateStandardOptionRequestDto.class,
    PageRequestDto.class,
    SearchMealRecordParamRequestDto.class,
    ErrorResponseDto.class,
    PageResponseMealRecordDto.class,
    MealTypeSimplifiedResponseDto.class,
    StandardOptionSimplifiedResponseDto.class,
    IdOnlyResponseDto.class,
    MealRecordCreatedResponseDto.class,
    ProjectInfoResponseDto.class,
    GitHubReleaseResponseDto.class
})
public class SanJyApplication {

    private SanJyApplication() {}

    public static void main(String[] args) {
        final var application = new SpringApplication(SanJyApplication.class);
        application.addInitializers(new TimezoneInitializer());
        application.run(args);
    }
}
