package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.gorillaroxo.sanjy.server.core.exception.ExceptionCode;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.builder.DtoBuilders;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class DietPlanControllerIT extends IntegrationTestController {

    @BeforeAll
    void setup() {
        cleanUpDatabase();
    }

    @Override
    protected String getBaseUrl() {
        return "/v1/diet-plan";
    }

    @Nested
    @DisplayName("POST /v1/diet-plan - newDietPlan")
    class NewDietPlan {

        @Test
        void should_create_diet_plan() {
            dietPlanRepository.deleteAll();
            final var request = DtoBuilders.buildCreateDietPlanRequestDto().build();
            final var timeFormatter = DateTimeFormatter.ofPattern(RequestConstants.DateTimeFormats.TIME_FORMAT);

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
                        // DietPlanCompleteResponseDto root fields
                        assertThat(response.id()).isNotNull();
                        assertThat(response.name()).isNotBlank().isEqualTo(request.name());
                        assertThat(response.startDate()).isEqualTo(request.startDate());
                        assertThat(response.endDate()).isEqualTo(request.endDate());
                        assertThat(response.dailyCalories()).isEqualTo(request.dailyCalories());
                        assertThat(response.dailyProteinInG()).isEqualTo(request.dailyProteinInG());
                        assertThat(response.dailyCarbsInG()).isEqualTo(request.dailyCarbsInG());
                        assertThat(response.dailyFatInG()).isEqualTo(request.dailyFatInG());
                        assertThat(response.goal()).isEqualTo(request.goal());
                        assertThat(response.nutritionistNotes()).isEqualTo(request.nutritionistNotes());
                        assertThat(response.isActive()).isTrue();

                        // DietPlanCompleteResponseDto.metadata
                        assertThat(response.metadata()).isNotNull();
                        assertThat(response.metadata().createdAt()).isNotNull();
                        assertThat(response.metadata().updatedAt()).isNotNull();

                        // DietPlanCompleteResponseDto.mealTypes
                        assertThat(response.mealTypes())
                                .isNotEmpty()
                                .hasSize(request.mealTypes().size());

                        final var requestMealType = request.mealTypes().getFirst();
                        final var responseMealType = response.mealTypes().getFirst();

                        // MealTypeResponseDto fields
                        assertThat(responseMealType.id()).isNotNull();
                        assertThat(responseMealType.name()).isEqualTo(requestMealType.name());
                        assertThat(responseMealType.scheduledTime().format(timeFormatter))
                                .isEqualTo(requestMealType.scheduledTime().format(timeFormatter));
                        assertThat(responseMealType.observation()).isEqualTo(requestMealType.observation());
                        assertThat(responseMealType.dietPlanId()).isEqualTo(response.id());

                        // MealTypeResponseDto.metadata
                        assertThat(responseMealType.metadata()).isNotNull();
                        assertThat(responseMealType.metadata().createdAt()).isNotNull();
                        assertThat(responseMealType.metadata().updatedAt()).isNotNull();

                        // MealTypeResponseDto.standardOptions
                        assertThat(responseMealType.standardOptions())
                                .isNotEmpty()
                                .hasSize(requestMealType.standardOptions().size());

                        final var requestStandardOption =
                                requestMealType.standardOptions().getFirst();
                        final var responseStandardOption =
                                responseMealType.standardOptions().getFirst();

                        // StandardOptionResponseDto fields
                        assertThat(responseStandardOption.id()).isNotNull();
                        assertThat(responseStandardOption.optionNumber())
                                .isEqualTo(requestStandardOption.optionNumber());
                        assertThat(responseStandardOption.description()).isEqualTo(requestStandardOption.description());
                        assertThat(responseStandardOption.mealTypeId()).isEqualTo(responseMealType.id());

                        // StandardOptionResponseDto.metadata
                        assertThat(responseStandardOption.metadata()).isNotNull();
                        assertThat(responseStandardOption.metadata().createdAt())
                                .isNotNull();
                        assertThat(responseStandardOption.metadata().updatedAt())
                                .isNotNull();
                    });
        }

        @Test
        void should_fail_when_passing_two_mealTypes_with_same_name() {
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
                        assertThat(response.timestamp()).isNotNull();
                        assertThat(response.message()).isNotEmpty().isEqualTo(expectedExCode.getMessage());
                        assertThat(response.customMessage()).isNotEmpty().containsIgnoringCase(repeatedName);
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    });
        }

        @Test
        void should_fail_with_invalid_startDate_format() {
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "name": "Test Diet Plan",
                              "startDate": "2025-13-45",
                              "endDate": "2025-12-31",
                              "mealTypes": [
                                {
                                  "name": "Breakfast",
                                  "standardOptions": [
                                    {
                                      "optionNumber": 1,
                                      "description": "Oatmeal"
                                    }
                                  ]
                                }
                              ]
                            }
                            """)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.INVALID_VALUES.getCode());
                        assertThat(response.message()).isEqualTo(ExceptionCode.INVALID_VALUES.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(response.customMessage()).containsIgnoringCase("startDate");
                        assertThat(response.customMessage()).containsIgnoringCase("errorMotive");
                    });
        }

        @Test
        void should_fail_with_invalid_endDate_format() {
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "name": "Test Diet Plan",
                              "startDate": "2025-01-01",
                              "endDate": "not-a-date",
                              "mealTypes": [
                                {
                                  "name": "Breakfast",
                                  "standardOptions": [
                                    {
                                      "optionNumber": 1,
                                      "description": "Oatmeal"
                                    }
                                  ]
                                }
                              ]
                            }
                            """)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.INVALID_VALUES.getCode());
                        assertThat(response.message()).isEqualTo(ExceptionCode.INVALID_VALUES.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(response.customMessage()).containsIgnoringCase("endDate");
                        assertThat(response.customMessage()).containsIgnoringCase("errorMotive");
                    });
        }

        @Test
        void should_fail_with_malformed_json() {
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "name": "Test Diet Plan",
                              "mealTypes": [
                                {
                                  "name": "Breakfast",
                                  "standardOptions": [
                            """)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.INVALID_VALUES.getCode());
                        assertThat(response.message()).isEqualTo(ExceptionCode.INVALID_VALUES.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    });
        }

        @Test
        void should_fail_with_number_instead_of_string_for_name() {
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "name": 12345,
                              "mealTypes": [
                                {
                                  "name": "Breakfast",
                                  "standardOptions": [
                                    {
                                      "optionNumber": 1,
                                      "description": "Oatmeal"
                                    }
                                  ]
                                }
                              ]
                            }
                            """)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.UNEXPECTED_ERROR.getCode());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    });
        }

        @Test
        void should_fail_with_string_instead_of_integer_for_dailyCalories() {
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "name": "Test Diet Plan",
                              "dailyCalories": "not-a-number",
                              "mealTypes": [
                                {
                                  "name": "Breakfast",
                                  "standardOptions": [
                                    {
                                      "optionNumber": 1,
                                      "description": "Oatmeal"
                                    }
                                  ]
                                }
                              ]
                            }
                            """)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.INVALID_VALUES.getCode());
                        assertThat(response.message()).isEqualTo(ExceptionCode.INVALID_VALUES.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(response.customMessage()).containsIgnoringCase("dailyCalories");
                        assertThat(response.customMessage()).containsIgnoringCase("invalid format");
                    });
        }

        @Test
        void should_fail_with_boolean_instead_of_integer_for_optionNumber() {
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "name": "Test Diet Plan",
                              "mealTypes": [
                                {
                                  "name": "Breakfast",
                                  "standardOptions": [
                                    {
                                      "optionNumber": true,
                                      "description": "Oatmeal"
                                    }
                                  ]
                                }
                              ]
                            }
                            """)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.INVALID_VALUES.getCode());
                        assertThat(response.message()).isEqualTo(ExceptionCode.INVALID_VALUES.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(response.customMessage()).containsIgnoringCase("JSON parse error");
                    });
        }
    }

    @Nested
    @DisplayName("GET /v1/diet-plan/active - activeDietPlan")
    class ActiveDietPlan {

        @Test
        void should_return_active_diet_plan() {
            // Given
            dietPlanRepository.deleteAll();
            final var timeFormatter = DateTimeFormatter.ofPattern(RequestConstants.DateTimeFormats.TIME_FORMAT);
            final var dietPlanRequest1 = DtoBuilders.buildCreateDietPlanRequestDto()
                    .name("Old Diet Plan")
                    .mealTypes(
                            List.of(DtoBuilders.buildCreateMealTypesRequestDto().build()))
                    .build();
            final var dietPlanRequest2 = DtoBuilders.buildCreateDietPlanRequestDto()
                    .name("New Diet Plan Test")
                    .mealTypes(List.of(
                            DtoBuilders.buildCreateMealTypesRequestDto()
                                    .name("one")
                                    .build(),
                            DtoBuilders.buildCreateMealTypesRequestDto()
                                    .name("two")
                                    .build(),
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
                        // DietPlanCompleteResponseDto root fields
                        assertThat(response.id()).isNotNull();
                        assertThat(response.name()).isNotBlank().isEqualTo(dietPlanRequest2.name());
                        assertThat(response.startDate()).isEqualTo(dietPlanRequest2.startDate());
                        assertThat(response.endDate()).isEqualTo(dietPlanRequest2.endDate());
                        assertThat(response.dailyCalories()).isEqualTo(dietPlanRequest2.dailyCalories());
                        assertThat(response.dailyProteinInG()).isEqualTo(dietPlanRequest2.dailyProteinInG());
                        assertThat(response.dailyCarbsInG()).isEqualTo(dietPlanRequest2.dailyCarbsInG());
                        assertThat(response.dailyFatInG()).isEqualTo(dietPlanRequest2.dailyFatInG());
                        assertThat(response.goal()).isEqualTo(dietPlanRequest2.goal());
                        assertThat(response.nutritionistNotes()).isEqualTo(dietPlanRequest2.nutritionistNotes());
                        assertThat(response.isActive()).isTrue();

                        // DietPlanCompleteResponseDto.metadata
                        assertThat(response.metadata()).isNotNull();
                        assertThat(response.metadata().createdAt()).isNotNull();
                        assertThat(response.metadata().updatedAt()).isNotNull();

                        // DietPlanCompleteResponseDto.mealTypes
                        assertThat(response.mealTypes())
                                .isNotEmpty()
                                .hasSize(dietPlanRequest2.mealTypes().size());

                        // Validate each MealType
                        for (int i = 0; i < dietPlanRequest2.mealTypes().size(); i++) {
                            final var requestMealType =
                                    dietPlanRequest2.mealTypes().get(i);
                            final var responseMealType = response.mealTypes().get(i);

                            // MealTypeResponseDto fields
                            assertThat(responseMealType.id()).isNotNull();
                            assertThat(responseMealType.name()).isEqualTo(requestMealType.name());
                            assertThat(responseMealType.scheduledTime().format(timeFormatter))
                                    .isEqualTo(requestMealType.scheduledTime().format(timeFormatter));
                            assertThat(responseMealType.observation()).isEqualTo(requestMealType.observation());
                            assertThat(responseMealType.dietPlanId()).isEqualTo(response.id());

                            // MealTypeResponseDto.metadata
                            assertThat(responseMealType.metadata()).isNotNull();
                            assertThat(responseMealType.metadata().createdAt()).isNotNull();
                            assertThat(responseMealType.metadata().updatedAt()).isNotNull();

                            // MealTypeResponseDto.standardOptions
                            assertThat(responseMealType.standardOptions())
                                    .isNotEmpty()
                                    .hasSize(requestMealType.standardOptions().size());

                            // Validate each StandardOption
                            for (int j = 0;
                                    j < requestMealType.standardOptions().size();
                                    j++) {
                                final var requestStandardOption =
                                        requestMealType.standardOptions().get(j);
                                final var responseStandardOption =
                                        responseMealType.standardOptions().get(j);

                                // StandardOptionResponseDto fields
                                assertThat(responseStandardOption.id()).isNotNull();
                                assertThat(responseStandardOption.optionNumber())
                                        .isEqualTo(requestStandardOption.optionNumber());
                                assertThat(responseStandardOption.description())
                                        .isEqualTo(requestStandardOption.description());
                                assertThat(responseStandardOption.mealTypeId()).isEqualTo(responseMealType.id());

                                // StandardOptionResponseDto.metadata
                                assertThat(responseStandardOption.metadata()).isNotNull();
                                assertThat(responseStandardOption.metadata().createdAt())
                                        .isNotNull();
                                assertThat(responseStandardOption.metadata().updatedAt())
                                        .isNotNull();
                            }
                        }
                    });
        }

        @Test
        void should_return_not_found_when_no_diet_plan_is_created() {
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
                        assertThat(response.timestamp()).isNotNull();
                        assertThat(response.message()).isNotEmpty().isEqualTo(expectedExCode.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                    });
        }
    }

    @Nested
    @DisplayName("Required headers validation")
    class RequiredHeaders {

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

            // WebTestClient/Netty doesn't allow empty or whitespace-only header values
            // Testing with invalid UUID format instead
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(headerNameXCorrelationId, "invalid-uuid")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
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
