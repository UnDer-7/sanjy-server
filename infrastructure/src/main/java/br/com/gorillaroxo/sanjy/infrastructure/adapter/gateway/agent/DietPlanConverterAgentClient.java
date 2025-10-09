package br.com.gorillaroxo.sanjy.infrastructure.adapter.gateway.agent;

import br.com.gorillaroxo.sanjy.core.domain.plan.DietPlanDomain;
import br.com.gorillaroxo.sanjy.core.ports.driven.DietPlanConverterAgentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DietPlanConverterAgentClient implements DietPlanConverterAgentGateway {

    @Qualifier("dietPlanConverterChatClient")
    private final ChatClient chatClient;

    @Override
    public DietPlanDomain convert(final String inputMessage) {
        return chatClient
            .prompt()
            .user(inputMessage)
            .call()
            .entity(DietPlanDomain.class);
    }

}
