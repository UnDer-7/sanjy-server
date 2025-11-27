package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.gorillaroxo.sanjy.server.core.exception.ExceptionCode;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.builder.DtoBuilders;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Integration tests for DietPlanController using WebTestClient. Compatible with both JVM and GraalVM Native Image
 * modes.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DietPlanControllerIT extends IntegrationTestController {

    @BeforeAll
    void setup() {
        cleanUpDatabase();
    }

    @Override
    protected String getBaseUrl() {
        return "/v1/diet-plan";
    }

    @Test
    @Order(1)
    void newDietPlan__should_create_diet_plan() {
        final var request = DtoBuilders.buildCreateDietPlanRequestDto().build();

        webTestClient
                .post()
                .uri(getBaseUrl())
                .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(DietPlanCompleteResponseDto.class)
                .value(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.name()).isNotBlank().isEqualTo(request.name());
                    assertThat(response.mealTypes())
                            .isNotEmpty()
                            .hasSize(request.mealTypes().size());
                    assertThat(response.isActive()).isTrue();
                });
    }

    @Test
    @Order(2)
    void newDietPlan__should_fail_when_passing_two_mealTypes_with_same_name() {
        final String repeatedName = "dinner";
        final var request = DtoBuilders.buildCreateDietPlanRequestDto()
                .name("New Diet Plan Test")
                .mealTypes(List.of(
                        DtoBuilders.buildCreateMealTypesRequestDto()
                                .name(repeatedName)
                                .build(),
                        DtoBuilders.buildCreateMealTypesRequestDto()
                                .name(repeatedName)
                                .build()))
                .build();

        webTestClient
                .post()
                .uri(getBaseUrl())
                .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .value(response -> {
                    final var expectedExCode = ExceptionCode.REPEATED_MEAL_TYPE_NAMES;
                    assertThat(response.code()).isNotBlank().isEqualTo(expectedExCode.getCode());
                    assertThat(response.timestamp()).isNotBlank();
                    assertThat(response.message()).isNotEmpty().isEqualTo(expectedExCode.getMessage());
                    assertThat(response.customMessage()).isNotEmpty().containsIgnoringCase(repeatedName);
                    assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
    }

    @Test
    @Order(3)
    void activeDietPlan__should_return_active_diet_plan() {
        // Given
        dietPlanRepository.deleteAll();
        final var dietPlanRequest1 = DtoBuilders.buildCreateDietPlanRequestDto()
                .name("Old Diet Plan")
                .mealTypes(List.of(DtoBuilders.buildCreateMealTypesRequestDto().build()))
                .build();
        final var dietPlanRequest2 = DtoBuilders.buildCreateDietPlanRequestDto()
                .name("New Diet Plan Test")
                .mealTypes(List.of(
                        DtoBuilders.buildCreateMealTypesRequestDto().name("one").build(),
                        DtoBuilders.buildCreateMealTypesRequestDto().name("two").build(),
                        DtoBuilders.buildCreateMealTypesRequestDto()
                                .name("three")
                                .build()))
                .build();

        webTestClient
                .post()
                .uri(getBaseUrl())
                .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dietPlanRequest1)
                .exchange()
                .expectStatus()
                .isCreated();
        webTestClient
                .post()
                .uri(getBaseUrl())
                .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dietPlanRequest2)
                .exchange()
                .expectStatus()
                .isCreated();

        // When/Then
        webTestClient
                .get()
                .uri(getBaseUrl() + "/active")
                .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(DietPlanCompleteResponseDto.class)
                .value(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.name()).isNotBlank().isEqualTo(dietPlanRequest2.name());
                    assertThat(response.mealTypes())
                            .isNotEmpty()
                            .hasSize(dietPlanRequest2.mealTypes().size());
                    assertThat(response.isActive()).isTrue();
                });
    }

    @Test
    @Order(3)
    void activeDietPlan__should_return_not_found_when_no_diet_plan_is_created() {
        // Given
        dietPlanRepository.deleteAll();
        // When/Then
        webTestClient
                .get()
                .uri(getBaseUrl() + "/active")
                .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ErrorResponseDto.class)
                .value(response -> {
                    final var expectedExCode = ExceptionCode.DIET_PLAN_NOT_FOUND;
                    assertThat(response.code()).isNotBlank().isEqualTo(expectedExCode.getCode());
                    assertThat(response.timestamp()).isNotBlank();
                    assertThat(response.message()).isNotEmpty().isEqualTo(expectedExCode.getMessage());
                    assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    @Nested
    @DisplayName("Test default required headers")
    class InvalidHeaders {

        @Test
        void should_fail_when_missing_required_headers() {
            // WebTestClient/Netty doesn't allow empty or whitespace-only header values
            // Testing without required headers instead
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .contentType(MediaType.APPLICATION_JSON)
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
                    .post()
                    .uri(getBaseUrl())
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
                    .value(msg -> assertThat(msg.toString())
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
                    .uri(getBaseUrl())
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
                        assertThat(message).containsIgnoringCase(headerNameXCorrelationId);
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
                    .uri(getBaseUrl())
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
                        assertThat(message).containsIgnoringCase(headerNameXChannel);
                    });
        }
    }
}
