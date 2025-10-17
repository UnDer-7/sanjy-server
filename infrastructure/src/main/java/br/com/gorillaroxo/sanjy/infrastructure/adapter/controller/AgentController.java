//package br.com.gorillaroxo.sanjy.infrastructure.adapter.controller;
//
//import br.com.gorillaroxo.sanjy.core.ports.driver.AgentSanjyUseCase;
//import br.com.gorillaroxo.sanjy.entrypoint.dto.request.AgentRequestDTO;
//import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.AgentResponseDTO;
//import br.com.gorillaroxo.sanjy.entrypoint.rest.AgentRestService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@Validated
//@RestController
//@RequiredArgsConstructor
//public class AgentController implements AgentRestService {
//
//    private final AgentSanjyUseCase agentUseCase;
//
//    @Override
//    public AgentResponseDTO agentSanjy(@RequestBody final AgentRequestDTO request) {
//        final String response = agentUseCase.execute(request.inputMessage());
//        return new AgentResponseDTO(response);
//    }
//
//}
