package br.com.gorillaroxo.sanjy.server.infrastructure.test;

import br.com.gorillaroxo.sanjy.server.infrastructure.SanJyApplication;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.config.TestcontainersConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Base class for integration tests. Uses WebTestClient which is compatible with both JVM and GraalVM Native Image
 * modes.
 */
@Slf4j
@SpringBootTest(classes = SanJyApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestcontainersConfig.class})
public abstract class IntegrationTestController {

    @Autowired
    protected WebTestClient webTestClient;
}
