package br.com.gorillaroxo.sanjy.infrastructure.config;

import br.com.gorillaroxo.sanjy.infrastructure.adapter.controller.DietPlanController;
import br.com.gorillaroxo.sanjy.infrastructure.adapter.controller.MealRecordController;
import br.com.gorillaroxo.sanjy.infrastructure.chat.tool.SanjyAgentTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class McpConfig {

    private final @Lazy Set<SanjyAgentTool> tools;

//    @Bean
//    public ToolCallbackProvider weatherTools(final Set<SanjyAgentTool> tools) {
//        return MethodToolCallbackProvider.builder()
//            .toolObjects(tools.toArray(SanjyAgentTool[]::new))
//            .build();
//    }

    @Bean
    public ToolCallbackProvider weatherTools() {
        return MethodToolCallbackProvider.builder()
            .toolObjects(tools.toArray(SanjyAgentTool[]::new))
//            .toolObjects(tools)
            .build();
    }
}
