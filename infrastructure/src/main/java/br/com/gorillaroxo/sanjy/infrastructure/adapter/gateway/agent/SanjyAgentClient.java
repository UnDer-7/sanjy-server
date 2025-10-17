//package br.com.gorillaroxo.sanjy.infrastructure.adapter.gateway.agent;
//
//import br.com.gorillaroxo.sanjy.core.ports.driven.SanjyAgentGateway;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.memory.ChatMemory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class SanjyAgentClient implements SanjyAgentGateway {
//
//    @Qualifier("sanjyChatClient")
//    private final ChatClient chatClient;
//
//    @Override
//    public String ask(final String question, final Long dietPlanId) {
//        final String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
//
//        return chatClient
//            .prompt()
//            .system(sp -> sp.param("currentTime", currentTime))
//            .user(question)
//            // todo: ver melhor como salvar CONVERSATION_ID
//            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, ChatMemory.DEFAULT_CONVERSATION_ID))
////            .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "planId == %s".formatted(dietPlanId)))
//            .call()
//            .content();
//    }
//}
