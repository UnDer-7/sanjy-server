package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordStatisticsDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.PageResultDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.SearchMealRecordParamDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetMealRecordStatisticsUseCase;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetTodayMealRecordsUseCase;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.RegisterMealRecordUseCase;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.SearchMealRecordUseCase;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealRecordRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.SearchMealRecordParamRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordStatisticsResponseDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.PageResponseDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.rest.MealRecordRestService;
import br.com.gorillaroxo.sanjy.server.infrastructure.config.McpToolMarker;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.MealRecordMapper;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.PageMapper;
import java.time.LocalDateTime;
import java.util.List;
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
public class MealRecordController implements MealRecordRestService, McpToolMarker {

    private final SearchMealRecordUseCase searchMealRecordUseCase;
    private final RegisterMealRecordUseCase registerMealRecordUseCase;
    private final GetTodayMealRecordsUseCase getTodayMealRecordsUseCase;
    private final GetMealRecordStatisticsUseCase getMealRecordStatisticsUseCase;
    private final MealRecordMapper mealRecordMapper;
    private final PageMapper pageMapper;

    @Override
    @PostMapping("/v1/meal-record")
    @ResponseStatus(HttpStatus.CREATED)
    @Tool(name = "newMealRecord", description = """
            Records a meal consumption with timestamp, meal type, and quantity. Can register either a standard meal \
            (following the diet plan by referencing a standard option) or a free meal (off-plan with custom description).
            """)
    public MealRecordResponseDTO newMealRecord(final CreateMealRecordRequestDto request) {
        log.info(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request to create a new meal record"));
        log.debug(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request body to create a new meal record"),
                StructuredArguments.kv(LogField.REQUEST_BODY.label(), "( " + request + " )"));

        final var mealRecord = mealRecordMapper.toDomain(request);
        final MealRecordDomain mealRecordSaved = registerMealRecordUseCase.execute(mealRecord);
        final MealRecordResponseDTO responseDto = mealRecordMapper.toDTO(mealRecordSaved);

        log.debug(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Response body meal record"),
                StructuredArguments.kv(LogField.RESPONSE_BODY.label(), "( " + responseDto + " )"));

        return responseDto;
    }

    @Override
    @GetMapping("/v1/meal-record/today")
    @Tool(name = "getTodayMealRecords", description = """
            Retrieves all meals consumed today, ordered by consumption time. Includes both standard meals (following the diet plan) \
            and free meals (off-plan). Use this to check daily food intake and diet adherence.
            """)
    public List<MealRecordResponseDTO> getTodayMealRecords() {
        log.info(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request to get today meal records"));

        final List<MealRecordDomain> mealRecords = getTodayMealRecordsUseCase.execute();
        final List<MealRecordResponseDTO> responseDTO = mealRecordMapper.toDTO(mealRecords);

        log.debug(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Response today meal records"),
                StructuredArguments.kv(LogField.RESPONSE_BODY.label(), "( " + responseDTO + " )"));

        return responseDTO;
    }

    @Override
    @GetMapping("/v1/meal-record")
    @Tool(name = "searchMealRecords", description = """
            Searches meal records with pagination and optional filters (date range, meal type). Returns paginated results with total count. \
            Use this to view historical meal data, analyze eating patterns, or generate reports.
            """)
    public PageResponseDTO<MealRecordResponseDTO> searchMealRecords(final SearchMealRecordParamRequestDTO pageRequest) {
        log.info(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request to search meal records"));
        log.debug(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request query-param to search meal records"),
                StructuredArguments.kv(LogField.MSG.label(), "( " + pageRequest + " )"));

        final SearchMealRecordParamDomain pageRequestDomain = pageMapper.toDomain(pageRequest);
        final PageResultDomain<MealRecordDomain> pageResult = searchMealRecordUseCase.execute(pageRequestDomain);
        final PageResponseDTO<MealRecordResponseDTO> responseDTO = pageMapper.toDTO(pageResult);

        log.debug(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Response search meal records"),
                StructuredArguments.kv(LogField.RESPONSE_BODY.label(), "( " + responseDTO + " )"));

        return responseDTO;
    }

    @Override
    @GetMapping("/v1/meal-record/statistics")
    @Tool(name = "getMealRecordStatistics", description = """
            Retrieves aggregated statistics of meal records within a specified date range. Returns the total count of meals consumed, \
            broken down by free meals (off-plan) and planned meals (following the diet plan). Use this to analyze diet adherence, \
            track compliance with the meal plan, or generate summary reports for a specific period.
            """)
    public MealRecordStatisticsResponseDTO getMealRecordStatisticsByDateRange(
            final LocalDateTime consumedAtAfter, final LocalDateTime consumedAtBefore) {
        log.info(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request to get meal record statistics by date range"));
        log.debug(
                LogField.Placeholders.THREE.getPlaceholder(),
                StructuredArguments.kv(
                        LogField.MSG.label(), "Request query-param to get meal record statistics by date range"),
                StructuredArguments.kv(LogField.CONSUMED_AT_AFTER.label(), consumedAtAfter),
                StructuredArguments.kv(LogField.CONSUMED_AT_BEFORE.label(), consumedAtBefore));

        final MealRecordStatisticsDomain statistics =
                getMealRecordStatisticsUseCase.execute(consumedAtAfter, consumedAtBefore);
        final MealRecordStatisticsResponseDTO responseDTO = mealRecordMapper.toDTO(statistics);

        log.debug(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Response meal record statistics"),
                StructuredArguments.kv(LogField.RESPONSE_BODY.label(), "( " + responseDTO + " )"));

        return responseDTO;
    }
}
