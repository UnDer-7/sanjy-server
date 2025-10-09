package br.com.gorillaroxo.sanjy.entrypoint.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record AgentRequestDTO(
    @NotBlank String inputMessage
) {

}
