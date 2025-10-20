package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.PageResultDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.SearchMealRecordParamDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetTodayMealRecordsUseCase;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.RegisterMealRecordUseCase;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.SearchMealRecordUseCase;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.SearchMealRecordParamRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.PageResponseDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.rest.MealRecordRestService;
import br.com.gorillaroxo.sanjy.server.infrastructure.config.McpToolMarker;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.MealRecordMapper;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class MealRecordController implements MealRecordRestService, McpToolMarker {

    private final RegisterMealRecordUseCase registerMealRecordUseCase;
    private final GetTodayMealRecordsUseCase getTodayMealRecordsUseCase;
    private final SearchMealRecordUseCase searchMealRecordUseCase;
    private final MealRecordMapper mealRecordMapper;
    private final PageMapper pageMapper;

    @Override
    @PostMapping("/v1/meal-record")
    @ResponseStatus(HttpStatus.CREATED)
    @Tool(name = "newMealRecord", description = "Registers a new meal record")
    public MealRecordResponseDTO newMealRecord(final CreateMealRecordRequestDTO request) {
        log.info("Registering a new meal record...");
        final var mealRecord = mealRecordMapper.toDomain(request);
        final MealRecordDomain mealRecordSaved = registerMealRecordUseCase.execute(mealRecord);
        return mealRecordMapper.toDTO(mealRecordSaved);
    }

    @Override
    @GetMapping("/v1/meal-record/today")
    @Tool(name = "getTodayMealRecords", description = "Retrieves all meal records for today")
    public List<MealRecordResponseDTO> getTodayMealRecords() {
        log.info("Retrieving all the diet plans for today...");
        final List<MealRecordDomain> mealRecords = getTodayMealRecordsUseCase.execute();
        return mealRecordMapper.toDTO(mealRecords);
    }

    @Override
    @GetMapping("/v1/meal-record")
    @Tool(name = "searchMealRecords", description = "Searches meal records")
    public PageResponseDTO<MealRecordResponseDTO> searchMealRecords(final SearchMealRecordParamRequestDTO pageRequest) {
        log.info("Searching meal records...");
        final SearchMealRecordParamDomain pageRequestDomain = pageMapper.toDomain(pageRequest);
        final PageResultDomain<MealRecordDomain> pageResult = searchMealRecordUseCase.execute(pageRequestDomain);
        return pageMapper.toDTO(pageResult);
    }

}
