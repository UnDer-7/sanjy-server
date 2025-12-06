package br.com.gorillaroxo.sanjy.server.infrastructure.test;

import br.com.gorillaroxo.sanjy.server.infrastructure.SanJyApplication;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.DietPlanEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository.DietPlanRepository;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository.MealRecordRepository;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.builder.EntityBuilders;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.config.TestcontainersConfig;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Base class for integration tests. Uses WebTestClient which is compatible with both JVM and GraalVM Native Image
 * modes.
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = SanJyApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestcontainersConfig.class})
public abstract class IntegrationTestController {

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected DietPlanRepository dietPlanRepository;

    @Autowired
    protected MealRecordRepository mealRecordRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected abstract String getBaseUrl();

    protected final void cleanUpDatabase() {
        jdbcTemplate.execute("""
                TRUNCATE TABLE
                    meal_record CASCADE;

                TRUNCATE TABLE
                    standard_options CASCADE;

                TRUNCATE TABLE
                    meal_type CASCADE;

                TRUNCATE TABLE
                    diet_plan CASCADE;

                -- Reset sequences to restart from 1
                ALTER SEQUENCE diet_plan_id_seq RESTART WITH 1;

                ALTER SEQUENCE meal_type_id_seq RESTART WITH 1;

                ALTER SEQUENCE standard_options_id_seq RESTART WITH 1;

                ALTER SEQUENCE meal_record_id_seq RESTART WITH 1;
                """);
    }

    protected final DietPlanEntity createDietPlan() {
        final var standardOption =
                EntityBuilders.buildStandardOptionEntity().id(null).build();
        final var mealType = EntityBuilders.buildMealTypeEntity()
                .id(null)
                .standardOptions(Set.of(standardOption))
                .build();
        final var dietPlan = EntityBuilders.buildDietPlanEntity()
                .id(null)
                .mealTypes(Set.of(mealType))
                .build();
        return dietPlanRepository.save(dietPlan);
    }
}
