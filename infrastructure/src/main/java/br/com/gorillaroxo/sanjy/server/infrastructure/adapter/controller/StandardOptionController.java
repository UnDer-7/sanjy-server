package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.domain.PatchableStandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.core.usecase.PatchStandardOptionUseCase;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.UpdateStandardOptionRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.StandardOptionResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.rest.StandardOptionRestService;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller.config.SanjyEndpoint;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.StandardOptionMapper;
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
@SanjyEndpoint("/v1/standard-option")
@RequiredArgsConstructor
public class StandardOptionController implements StandardOptionRestService {

    private final PatchStandardOptionUseCase patchStandardOptionUseCase;
    private final StandardOptionMapper standardOptionMapper;

    @Override
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StandardOptionResponseDto updateStandardOption(
            @RequestBody final UpdateStandardOptionRequestDto requestBody,
            @PathVariable(RequestConstants.Path.ID) final Long id) {
        log.info(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request to patch standard option"),
                StructuredArguments.kv(LogField.STANDARD_OPTION_ID.label(), id));

        log.debug(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request body to patch standard option"),
                StructuredArguments.kv(LogField.REQUEST_BODY.label(), "( " + requestBody + " )"));

        final PatchableStandardOptionDomain domain = standardOptionMapper.toDomain(requestBody, id);
        final StandardOptionDomain updated = patchStandardOptionUseCase.execute(domain);
        return standardOptionMapper.toDto(updated);
    }
}
