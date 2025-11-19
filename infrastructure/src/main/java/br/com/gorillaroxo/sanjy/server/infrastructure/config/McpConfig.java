package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class McpConfig {

    private final Set<McpToolMarker> tools;

    @Bean
    public ToolCallbackProvider weatherTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(tools.toArray(McpToolMarker[]::new))
                .build();
    }
}
