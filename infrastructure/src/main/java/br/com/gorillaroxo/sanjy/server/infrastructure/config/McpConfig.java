package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller.DietPlanController;
import br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller.MealRecordController;
import br.com.gorillaroxo.sanjy.server.infrastructure.chat.tool.SanjyAgentTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class McpConfig {

    private final Set<SanjyAgentTool> tools;
    private final DietPlanController dietPlanController;
//    private final MealRecordController mealRecordController;

//    @Bean
//    public ToolCallbackProvider weatherTools(final Set<SanjyAgentTool> tools) {
//        return MethodToolCallbackProvider.builder()
//            .toolObjects(tools.toArray(SanjyAgentTool[]::new))
//            .build();
//    }

    @Bean
    public ToolCallbackProvider weatherTools(MealRecordController mealRecordController) {
        return MethodToolCallbackProvider.builder()
            .toolObjects(tools.toArray(SanjyAgentTool[]::new))
            .build();
    }
}
