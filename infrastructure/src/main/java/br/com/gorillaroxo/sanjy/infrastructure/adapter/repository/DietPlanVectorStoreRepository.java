package br.com.gorillaroxo.sanjy.infrastructure.adapter.repository;

import br.com.gorillaroxo.sanjy.core.ports.driven.DietPlanVectorStoreGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DietPlanVectorStoreRepository implements DietPlanVectorStoreGateway {

    private final VectorStore vectorStore;

    @Override
    public void upsertPlan(final String planText, final Long planId) {
        Optional<JdbcTemplate> nativeClient = vectorStore.getNativeClient();

//        if (nativeClient.isEmpty()) {
//            // todo: Jogar exception
//            throw new RuntimeException("Could not get native client for vector store.");
//        }
//
//        final JdbcTemplate jdbcTemplate = nativeClient.get();
//        final var type = "plan";
//
//        final List<String> ids = jdbcTemplate.queryForList(
//            "SELECT id FROM vector_store WHERE metadata->>'type' = ?",
//            String.class,
//            type);
//
//        log.info("Found {} records with type: {}", ids.size(), type);
//
//        int deletedRows = jdbcTemplate.update(
//            "DELETE FROM vector_store WHERE metadata->>'type' = ?",
//            type);
//
//        log.info("Deleted {} records with type: {}", deletedRows, type);

        vectorStore.add(List.of(
            new Document(planText, Map.of("type", "diet_plan", "planId", planId))
                               ));

    }
}
