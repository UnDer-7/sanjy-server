package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.core.exception.ExceptionCode;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.UpdateStandardOptionRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.StandardOptionResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.builder.DtoBuilders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"java:S5961", "Integration tests may have many assertions per method"})
class StandardOptionControllerIT extends IntegrationTestController {

    static final String BASE_URL = "/v1/standard-option";

    @BeforeAll
    void setup() {
        cleanUpDatabase();
    }

    @Nested
    @DisplayName("PATCH /v1/standard-option/{id} - updateStandardOption")
    class UpdateStandardOption {

        @Test
        void should_update_description() {
            // Given
            dietPlanRepository.deleteAll();

            final var createdPlan = webTestClient
                    .post()
                    .uri("/v1/diet-plan")
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(DtoBuilders.buildCreateDietPlanRequestDto().build())
                    .exchange()
                    .expectStatus()
                    .isCreated()
                    .expectBody(DietPlanCompleteResponseDto.class)
                    .returnResult()
                    .getResponseBody();
            assertThat(createdPlan).isNotNull();

            final var originalOption = createdPlan.mealTypes().getFirst().standardOptions().getFirst();
            final var patchRequest = DtoBuilders.buildUpdateStandardOptionRequestDto().build();

            // When/Then
            webTestClient
                    .patch()
                    .uri(BASE_URL + "/{id}", originalOption.id())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchRequest)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(StandardOptionResponseDto.class)
                    .value(response -> {
                        assertThat(response.id()).isEqualTo(originalOption.id());
                        assertThat(response.optionNumber()).isEqualTo(originalOption.optionNumber());
                        assertThat(response.description()).isEqualTo(patchRequest.description());
                        assertThat(response.mealTypeId()).isEqualTo(createdPlan.mealTypes().getFirst().id());
                        assertThat(response.metadata()).isNotNull();
                        assertThat(response.metadata().createdAt()).isNotNull();
                        assertThat(response.metadata().updatedAt()).isNotNull();
                    });
        }

        @Test
        void should_preserve_description_when_not_included_in_request() {
            // Given
            dietPlanRepository.deleteAll();

            final var createdPlan = webTestClient
                    .post()
                    .uri("/v1/diet-plan")
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(DtoBuilders.buildCreateDietPlanRequestDto().build())
                    .exchange()
                    .expectStatus()
                    .isCreated()
                    .expectBody(DietPlanCompleteResponseDto.class)
                    .returnResult()
                    .getResponseBody();
            assertThat(createdPlan).isNotNull();

            final var originalOption = createdPlan.mealTypes().getFirst().standardOptions().getFirst();
            final var patchRequest = UpdateStandardOptionRequestDto.builder().build();

            // When/Then
            webTestClient
                    .patch()
                    .uri(BASE_URL + "/{id}", originalOption.id())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchRequest)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(StandardOptionResponseDto.class)
                    .value(response -> {
                        assertThat(response.id()).isEqualTo(originalOption.id());
                        assertThat(response.optionNumber()).isEqualTo(originalOption.optionNumber());
                        assertThat(response.description()).isEqualTo(originalOption.description());
                    });
        }

        @Test
        void should_return_error_when_standard_option_not_found() {
            // Given
            dietPlanRepository.deleteAll();
            final var patchRequest = DtoBuilders.buildUpdateStandardOptionRequestDto().build();

            // When/Then
            webTestClient
                    .patch()
                    .uri(BASE_URL + "/{id}", 99999L)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchRequest)
                    .exchange()
                    .expectStatus()
                    .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value())
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        final var expectedExCode = ExceptionCode.STANDARD_OPTION_NOT_FOUND;
                        assertThat(response.code()).isEqualTo(expectedExCode.getCode());
                        assertThat(response.timestamp()).isNotNull();
                        assertThat(response.message()).isEqualTo(expectedExCode.getMessage());
                        assertThat(response.customMessage()).containsIgnoringCase("99999");
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
                    });
        }
    }
}
