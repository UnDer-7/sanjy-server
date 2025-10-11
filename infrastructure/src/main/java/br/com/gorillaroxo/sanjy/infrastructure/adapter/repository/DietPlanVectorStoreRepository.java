package br.com.gorillaroxo.sanjy.infrastructure.adapter.repository;

import br.com.gorillaroxo.sanjy.core.ports.driven.DietPlanVectorStoreGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DietPlanVectorStoreRepository implements DietPlanVectorStoreGateway {

    private final VectorStore vectorStore;

    @Override
    public void upsertPlan(final String planText, final Long planId) {
        vectorStore.add(List.of(
            new Document(planText, Map.of("type", "diet_plan", "planId", planId))
                               ));
    }

}
