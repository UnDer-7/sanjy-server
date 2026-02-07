package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.infrastructure.test.IntegrationTestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

class SwaggerIT extends IntegrationTestController {

    static final String SWAGGER_BASE_URL = "/swagger-ui/index.html";

    @Value("${springdoc.api-docs.path}")
    String apiDocsBaseUrl;

    @Test
    void swagger_test() {
        webTestClient
            .get()
            .uri(SWAGGER_BASE_URL)
            .exchange()
            .expectStatus().isCreated();
    }

    @Test
    void api_docs_test() {
        webTestClient
            .get()
            .uri(apiDocsBaseUrl)
            .exchange()
            .expectStatus().isCreated();
    }

}
