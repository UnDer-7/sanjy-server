package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.domain.MealTypeDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.PatchableMealTypeDomain;
import br.com.gorillaroxo.sanjy.server.core.usecase.PatchMealTypeUseCase;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.UpdateMealTypeRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealTypeResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.rest.MealTypeRestService;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller.config.SanjyEndpoint;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.MealTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@SanjyEndpoint("/v1/meal-type")
@RequiredArgsConstructor
public class MealTypeController implements MealTypeRestService {

    private final PatchMealTypeUseCase patchMealTypeUseCase;
    private final MealTypeMapper mealTypeMapper;

    @Override
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MealTypeResponseDto updateMealType(
            @RequestBody final UpdateMealTypeRequestDto requestBody,
            @PathVariable(RequestConstants.Path.ID) final Long id) {
        log.info(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request to patch meal type"),
                StructuredArguments.kv(LogField.MEAL_TYPE_ID.label(), id));

        log.debug(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request body to patch meal type"),
                StructuredArguments.kv(LogField.REQUEST_BODY.label(), "( " + requestBody + " )"));

        final PatchableMealTypeDomain domain = mealTypeMapper.toDomain(requestBody, id);
        final MealTypeDomain updated = patchMealTypeUseCase.execute(domain);
        return mealTypeMapper.toDto(updated);
    }
}
