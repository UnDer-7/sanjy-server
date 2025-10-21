package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponseDTO(
    String code,
    String timestamp,
    String message,
    String customMessage,
    int httpStatusCode
) {

}
