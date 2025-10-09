package br.com.gorillaroxo.sanjy.entrypoint.rest;

import br.com.gorillaroxo.sanjy.entrypoint.dto.request.AgentRequestDTO;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.AgentResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(
    name = "Agent"
)
public interface AgentRestService {

    @PostMapping("/v1/agent/sanjy")
    AgentResponseDTO agentSanjy(@NotNull @Valid AgentRequestDTO request);
}
