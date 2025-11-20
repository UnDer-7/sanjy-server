package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.CreateDietPlanUseCase;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetActiveDietPlanUseCase;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateDietPlanRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.rest.DietPlanRestService;
import br.com.gorillaroxo.sanjy.server.infrastructure.config.McpToolMarker;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.DietPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class DietPlanController implements DietPlanRestService, McpToolMarker {

    private final CreateDietPlanUseCase createDietPlanUseCase;
    private final GetActiveDietPlanUseCase getActiveDietPlanUseCase;
    private final DietPlanMapper dietPlanMapper;

    @Override
    @GetMapping("/v1/diet-plan/active")
    @Tool(name = "activeDietPlan", description = """
            Retrieves the currently active diet plan with all meal types, standard options, \
            nutritional targets (calories, protein, carbs, fat), and goals. Only one diet plan can be active at a time.
            """)
    public DietPlanCompleteResponseDto activeDietPlan() {
        log.info(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request to get active diet plan"));

        final DietPlanDomain dietPlan = getActiveDietPlanUseCase.execute();

        final DietPlanCompleteResponseDto dtoResponse = dietPlanMapper.toDto(dietPlan);

        log.debug(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Response body diet plan"),
                StructuredArguments.kv(LogField.RESPONSE_BODY.label(), "( " + dtoResponse + " )"));

        return dtoResponse;
    }

    @Override
    @PostMapping("/v1/diet-plan")
    @ResponseStatus(HttpStatus.CREATED)
    @Tool(name = "newDietPlan", description = """
            Creates a new diet plan with meal types (breakfast, lunch, snack, dinner, etc.), \
            standard meal options, nutritional targets, and goals. The new plan is automatically set as active and any previously active plan is deactivated.
            """)
    public DietPlanCompleteResponseDto newDietPlan(final CreateDietPlanRequestDto request) {
        log.info(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request to create a new diet plan"));
        log.debug(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request body to create a new diet plan"),
                StructuredArguments.kv(LogField.REQUEST_BODY.label(), "( " + request + " )"));

        final DietPlanDomain dietPlan = dietPlanMapper.toDomain(request);

        final DietPlanDomain dietPlanCreated = createDietPlanUseCase.execute(dietPlan);

        final DietPlanCompleteResponseDto dtoResponse = dietPlanMapper.toDto(dietPlanCreated);

        log.debug(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Response body diet plan"),
                StructuredArguments.kv(LogField.RESPONSE_BODY.label(), "( " + dtoResponse + " )"));

        return dtoResponse;
    }
}
