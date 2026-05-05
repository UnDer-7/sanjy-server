package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.core.exception.ExceptionCode;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.UpdateMealTypeRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealTypeResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.builder.DtoBuilders;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"java:S5961", "Integration tests may have many assertions per method"})
class MealTypeControllerIT extends IntegrationTestController {

    static final String BASE_URL = "/v1/meal-type";

    @BeforeAll
    void setup() {
        cleanUpDatabase();
    }

    @Nested
    @DisplayName("PATCH /v1/meal-type/{id} - updateMealType")
    class UpdateMealType {

        @Test
        void should_update_all_fields() {
            // Given
            dietPlanRepository.deleteAll();
            final var timeFormatter = DateTimeFormatter.ofPattern(RequestConstants.DateTimeFormats.TIME_FORMAT);

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

            final var originalMealType = createdPlan.mealTypes().getFirst();
            final var patchRequest = DtoBuilders.buildUpdateMealTypeRequestDto().build();

            // When/Then
            webTestClient
                    .patch()
                    .uri(BASE_URL + "/{id}", originalMealType.id())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchRequest)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(MealTypeResponseDto.class)
                    .value(response -> {
                        assertThat(response.id()).isEqualTo(originalMealType.id());
                        assertThat(response.name()).isEqualTo(patchRequest.name());
                        assertThat(response.scheduledTime().format(timeFormatter))
                                .isEqualTo(patchRequest.scheduledTime().format(timeFormatter));
                        assertThat(response.observation()).isEqualTo(patchRequest.observation());
                        assertThat(response.dietPlanId()).isEqualTo(createdPlan.id());
                        assertThat(response.metadata()).isNotNull();
                        assertThat(response.metadata().createdAt()).isNotNull();
                        assertThat(response.metadata().updatedAt()).isNotNull();

                        // StandardOptions preserved
                        assertThat(response.standardOptions())
                                .hasSize(originalMealType.standardOptions().size());
                        final var responseOption = response.standardOptions().getFirst();
                        final var originalOption = originalMealType.standardOptions().getFirst();
                        assertThat(responseOption.id()).isEqualTo(originalOption.id());
                        assertThat(responseOption.optionNumber()).isEqualTo(originalOption.optionNumber());
                        assertThat(responseOption.description()).isEqualTo(originalOption.description());
                        assertThat(responseOption.mealTypeId()).isEqualTo(originalMealType.id());
                    });
        }

        @Test
        void should_preserve_fields_not_included_in_request() {
            // Given
            dietPlanRepository.deleteAll();
            final var timeFormatter = DateTimeFormatter.ofPattern(RequestConstants.DateTimeFormats.TIME_FORMAT);

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

            final var originalMealType = createdPlan.mealTypes().getFirst();
            final var patchRequest = UpdateMealTypeRequestDto.builder()
                    .name("Dinner")
                    .build();

            // When/Then
            webTestClient
                    .patch()
                    .uri(BASE_URL + "/{id}", originalMealType.id())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchRequest)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(MealTypeResponseDto.class)
                    .value(response -> {
                        assertThat(response.id()).isEqualTo(originalMealType.id());
                        assertThat(response.name()).isEqualTo(patchRequest.name());
                        assertThat(response.scheduledTime().format(timeFormatter))
                                .isEqualTo(originalMealType.scheduledTime().format(timeFormatter));
                        assertThat(response.observation()).isEqualTo(originalMealType.observation());
                        assertThat(response.dietPlanId()).isEqualTo(createdPlan.id());
                        assertThat(response.metadata().createdAt().truncatedTo(ChronoUnit.MILLIS))
                                .isEqualTo(originalMealType.metadata().createdAt().truncatedTo(ChronoUnit.MILLIS));
                    });
        }

        @Test
        void should_return_error_when_meal_type_not_found() {
            // Given
            dietPlanRepository.deleteAll();
            final var patchRequest = DtoBuilders.buildUpdateMealTypeRequestDto().build();

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
                        final var expectedExCode = ExceptionCode.MEAL_TYPE_NOT_FOUND;
                        assertThat(response.code()).isEqualTo(expectedExCode.getCode());
                        assertThat(response.timestamp()).isNotNull();
                        assertThat(response.message()).isEqualTo(expectedExCode.getMessage());
                        assertThat(response.customMessage()).containsIgnoringCase("99999");
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
                    });
        }

        @Test
        void should_return_error_when_name_already_exists_in_same_diet_plan() {
            // Given
            dietPlanRepository.deleteAll();
            final String existingName = "Snack";

            final var createdPlan = webTestClient
                    .post()
                    .uri("/v1/diet-plan")
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(DtoBuilders.buildCreateDietPlanRequestDto()
                            .mealTypes(List.of(
                                    DtoBuilders.buildCreateMealTypesRequestDto().name("Breakfast").build(),
                                    DtoBuilders.buildCreateMealTypesRequestDto().name(existingName).build()))
                            .build())
                    .exchange()
                    .expectStatus()
                    .isCreated()
                    .expectBody(DietPlanCompleteResponseDto.class)
                    .returnResult()
                    .getResponseBody();
            assertThat(createdPlan).isNotNull();

            final var breakfastId = createdPlan.mealTypes().stream()
                    .filter(mt -> mt.name().equals("Breakfast"))
                    .findFirst()
                    .orElseThrow()
                    .id();

            final var patchRequest = UpdateMealTypeRequestDto.builder()
                    .name(existingName)
                    .build();

            // When/Then
            webTestClient
                    .patch()
                    .uri(BASE_URL + "/{id}", breakfastId)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchRequest)
                    .exchange()
                    .expectStatus()
                    .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value())
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        final var expectedExCode = ExceptionCode.MEAL_TYPE_DUPLICATE_NAME;
                        assertThat(response.code()).isEqualTo(expectedExCode.getCode());
                        assertThat(response.timestamp()).isNotNull();
                        assertThat(response.message()).isEqualTo(expectedExCode.getMessage());
                        assertThat(response.customMessage()).containsIgnoringCase(existingName);
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
                    });
        }
    }
}
