//package br.com.gorillaroxo.sanjy.core.usecase;
//
//import br.com.gorillaroxo.sanjy.core.ports.driven.DietPlanGateway;
//import br.com.gorillaroxo.sanjy.core.ports.driven.SanjyAgentGateway;
//import br.com.gorillaroxo.sanjy.core.ports.driver.AgentSanjyUseCase;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AgentSanjyUseCaseImpl implements AgentSanjyUseCase {
//
//    private final SanjyAgentGateway sanjyAgentGateway;
//    private final DietPlanGateway dietPlanGateway;
//
//    @Override
//    public String execute(final String inputMessage) {
//        // todo: jogar exception
//        final Long id = dietPlanGateway.findActive().orElseThrow().getId();
//
//        return sanjyAgentGateway.ask(inputMessage, id);
//    }
//
//}
