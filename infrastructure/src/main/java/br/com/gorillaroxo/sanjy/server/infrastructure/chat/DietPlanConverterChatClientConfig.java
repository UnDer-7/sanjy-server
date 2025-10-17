package br.com.gorillaroxo.sanjy.server.infrastructure.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DietPlanConverterChatClientConfig {

    private final ChatClient.Builder ai;

    @Bean
    public ChatClient dietPlanConverterChatClient() {
        final var systemMsg = """
            Extraia as informações do plano alimentar abaixo e retorne no formato JSON especificado.
            
            ESTRUTURA DO JSON:
            - Um objeto raiz representando o PLANO ALIMENTAR completo
            - Dentro dele, um array "mealType" com os TIPOS DE REFEIÇÃO
            - Cada tipo de refeição contém um array "standardOptions" com as OPÇÕES daquela refeição
            
            REGRAS OBRIGATÓRIAS:
            - NÃO inclua o campo "id" em nenhum objeto
            - NÃO invente dados. Use APENAS o que está explicitamente no texto
            - Se um campo não estiver no texto, deixe como null
            - Separe as opções por TIPO DE REFEIÇÃO
            - Cada tipo de refeição é um objeto diferente dentro do array "mealType"
            - No campo "description" de cada meal, separe os alimentos com " | " (pipe com espaços)
            """;

        return ai
            .defaultSystem(systemMsg)
            .build();
    }

}
