package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ProjectInfoResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.IntegrationTestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaintenanceControllerIT extends IntegrationTestController {

    static final String BASE_URL = "/v1/maintenance";

    @Nested
    @DisplayName("GET /v1/maintenance/project-info - projectInfo")
    class ProjectInfo {

        @Test
        void should_return_project_info() {
            webTestClient
                    .get()
                    .uri(BASE_URL + "/project-info")
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(ProjectInfoResponseDto.class)
                    .value(response -> {
                        // Version
                        assertThat(response.version()).isNotNull();
                        assertThat(response.version().current()).isNotBlank();
                        assertThat(response.version().isLatest()).isNotNull();

                        // Timezone
                        assertThat(response.timezone()).isNotNull();
                        assertThat(response.timezone().application()).isNotBlank();

                        // Runtime mode
                        assertThat(response.runtimeMode()).isNotBlank();
                    });
        }
    }

    @Nested
    @DisplayName("Required headers validation")
    class RequiredHeaders {

        @Test
        void should_fail_when_missing_required_headers() {
            webTestClient
                    .get()
                    .uri(BASE_URL + "/project-info")
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.customMessage")
                    .exists()
                    .jsonPath("$.customMessage")
                    .value(msg ->
                            assertThat(msg.toString()).containsIgnoringCase(RequestConstants.Headers.X_CORRELATION_ID));
        }

        @Test
        void should_fail_when_passing_invalid_uuid_correlation_id() {
            final String headerNameXCorrelationId = RequestConstants.Headers.X_CORRELATION_ID;

            webTestClient
                    .get()
                    .uri(BASE_URL + "/project-info")
                    .header(headerNameXCorrelationId, "7d1c9e48034744eca14256e77fc11dfe")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.customMessage")
                    .exists()
                    .jsonPath("$.customMessage")
                    .value(msg -> assertThat(msg.toString())
                            .containsIgnoringCase(headerNameXCorrelationId)
                            .containsIgnoringCase("valid UUID format"));
        }

        @Test
        void should_fail_when_only_passing_invalid_correlation_id() {
            final String headerNameXCorrelationId = RequestConstants.Headers.X_CORRELATION_ID;

            webTestClient
                    .get()
                    .uri(BASE_URL + "/project-info")
                    .header(headerNameXCorrelationId, "invalid-uuid")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.customMessage")
                    .exists()
                    .jsonPath("$.customMessage")
                    .value(msg -> {
                        String message = msg.toString();
                        assertThat(message).containsIgnoringCase(headerNameXCorrelationId);
                    });
        }

        @Test
        void should_fail_when_missing_xChannel() {
            final String headerNameXCorrelationId = RequestConstants.Headers.X_CORRELATION_ID;
            final String headerNameXChannel = RequestConstants.Headers.X_CHANNEL;

            webTestClient
                    .get()
                    .uri(BASE_URL + "/project-info")
                    .header(headerNameXCorrelationId, "7f804519-7a52-485c-983d-19439e5cc7a3")
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.customMessage")
                    .exists()
                    .jsonPath("$.customMessage")
                    .value(msg -> {
                        String message = msg.toString();
                        assertThat(message).containsIgnoringCase(headerNameXChannel);
                    });
        }
    }
}
