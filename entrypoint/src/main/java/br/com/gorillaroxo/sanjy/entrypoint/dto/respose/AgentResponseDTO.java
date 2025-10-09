package br.com.gorillaroxo.sanjy.entrypoint.dto.respose;

import lombok.Builder;

@Builder(toBuilder = true)
public record AgentResponseDTO(
    String responseMessage
) {

}
