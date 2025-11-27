package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.builder.DtoBuilders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/**
 * Integration tests for DietPlanController using WebTestClient. Compatible with both JVM and GraalVM Native Image
 * modes.
 */
class DietPlanControllerIT extends IntegrationTestController {

    private static final String BASE_URL = "/v1/diet-plan";

    @Nested
    @DisplayName("Test default required headers")
    class InvalidHeaders {

        @Test
        void should_fail_when_missing_required_headers() {
            // WebTestClient/Netty doesn't allow empty or whitespace-only header values
            // Testing without required headers instead
            webTestClient
                    .post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.customMessage")
                    .exists()
                    .jsonPath("$.customMessage")
                    .value(msg -> org.assertj.core.api.Assertions.assertThat(msg.toString())
                            .containsIgnoringCase(RequestConstants.Headers.X_CORRELATION_ID));
        }

        @Test
        void should_fail_when_passing_invalid_uuid_correlation_id() {
            final String headerNameXCorrelationId = RequestConstants.Headers.X_CORRELATION_ID;

            webTestClient
                    .post()
                    .uri(BASE_URL)
                    .header(headerNameXCorrelationId, "7d1c9e48034744eca14256e77fc11dfe")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.customMessage")
                    .exists()
                    .jsonPath("$.customMessage")
                    .value(msg -> org.assertj.core.api.Assertions.assertThat(msg.toString())
                            .containsIgnoringCase(headerNameXCorrelationId)
                            .containsIgnoringCase("valid UUID format"));
        }

        @Test
        void should_fail_when_only_passing_invalid_correlation_id() {
            final String headerNameXCorrelationId = RequestConstants.Headers.X_CORRELATION_ID;
            final String headerNameXChannel = RequestConstants.Headers.X_CHANNEL;

            // WebTestClient/Netty doesn't allow empty or whitespace-only header values
            // Testing with invalid UUID format instead
            webTestClient
                    .post()
                    .uri(BASE_URL)
                    .header(headerNameXCorrelationId, "invalid-uuid")
                    .header(headerNameXChannel, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.customMessage")
                    .exists()
                    .jsonPath("$.customMessage")
                    .value(msg -> {
                        String message = msg.toString();
                        org.assertj.core.api.Assertions.assertThat(message)
                                .containsIgnoringCase(headerNameXCorrelationId);
                    });
        }

        @Test
        void should_fail_when_missing_xChannel() {
            final String headerNameXCorrelationId = RequestConstants.Headers.X_CORRELATION_ID;
            final String headerNameXChannel = RequestConstants.Headers.X_CHANNEL;

            // WebTestClient/Netty doesn't allow empty or whitespace-only header values
            // Testing without X-Channel header instead
            webTestClient
                    .post()
                    .uri(BASE_URL)
                    .header(headerNameXCorrelationId, "7f804519-7a52-485c-983d-19439e5cc7a3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.customMessage")
                    .exists()
                    .jsonPath("$.customMessage")
                    .value(msg -> {
                        String message = msg.toString();
                        org.assertj.core.api.Assertions.assertThat(message).containsIgnoringCase(headerNameXChannel);
                    });
        }
    }

    @Test
    void should_create_diet_plan() {
        final var request = DtoBuilders.buildCreateDietPlanRequestDto().build();

        webTestClient
                .post()
                .uri(BASE_URL)
                .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();
    }
}
