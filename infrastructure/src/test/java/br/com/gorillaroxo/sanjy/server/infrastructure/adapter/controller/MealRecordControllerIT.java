package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.gorillaroxo.sanjy.server.core.exception.ExceptionCode;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordCreatedResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.PageResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.StandardOptionSimplifiedResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.DietPlanEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.MealTypeEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.StandardOptionEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.builder.DtoBuilders;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class MealRecordControllerIT extends IntegrationTestController {

    DietPlanEntity dietPlan;

    @BeforeAll
    void setup() {
        cleanUpDatabase();
        dietPlan = createDietPlan();
    }

    @Override
    protected String getBaseUrl() {
        return "/v1/meal-record";
    }

    @Nested
    @DisplayName("POST /v1/meal-record - newMealRecord")
    class NewMealRecord {

        @Test
        void should_successfully_create_planned_mealRecord() {
            final MealTypeEntity mealType =
                    dietPlan.getMealTypes().stream().findFirst().orElseThrow();
            final StandardOptionEntity standardOption =
                    mealType.getStandardOptions().stream().findFirst().orElseThrow();

            final var request = DtoBuilders.buildCreateMealRecordRequestDtoPlannedMeal()
                    .mealTypeId(mealType.getId())
                    .standardOptionId(standardOption.getId())
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
                    .isCreated()
                    .expectBody(MealRecordCreatedResponseDto.class)
                    .value(response -> {
                        // MealRecordResponseDto root fields
                        assertThat(response.id()).isNotNull();
                        assertThat(response.consumedAt()).isNotNull();
                        assertThat(response.isFreeMeal()).isEqualTo(request.isFreeMeal());
                        assertThat(response.freeMealDescription()).isEqualTo(request.freeMealDescription());
                        assertThat(response.quantity()).isEqualTo(request.quantity());
                        assertThat(response.unit()).isEqualTo(request.unit());
                        assertThat(response.notes()).isEqualTo(request.notes());

                        // MealRecordResponseDto.metadata
                        assertThat(response.metadata()).isNotNull();
                        assertThat(response.metadata().createdAt()).isNotNull();
                        assertThat(response.metadata().updatedAt()).isNotNull();

                        // MealRecordResponseDto.mealType (MealTypeSimplifiedResponseDto)
                        assertThat(response.mealType()).isNotNull();
                        assertThat(response.mealType().id()).isEqualTo(mealType.getId());

                        // MealRecordResponseDto.standardOption (StandardOptionSimplifiedResponseDto)
                        assertThat(response.standardOption()).isNotNull();
                        assertThat(response.standardOption().id()).isEqualTo(standardOption.getId());
                    });
        }

        @Test
        void should_fail_when_passing_null_mealType_id() {
            final var request = DtoBuilders.buildCreateMealRecordRequestDtoPlannedMeal()
                    .mealTypeId(null)
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
                        final var expectedExCode = ExceptionCode.INVALID_VALUES;
                        assertThat(response.code()).isNotBlank().isEqualTo(expectedExCode.getCode());
                        assertThat(response.timestamp()).isNotNull();
                        assertThat(response.message()).isNotEmpty().isEqualTo(expectedExCode.getMessage());
                        assertThat(response.customMessage()).isNotEmpty().containsIgnoringCase("mealTypeId");
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    });
        }

        @Test
        void should_fail_with_invalid_date_time_format() {
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "mealTypeId": 1,
                              "consumedAt": "2025-11-14T10:34:55",
                              "isFreeMeal": false,
                              "standardOptionId": 1
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
                        assertThat(response.customMessage()).containsIgnoringCase("consumedAt");
                        assertThat(response.customMessage()).containsIgnoringCase("invalid date-time format");
                        assertThat(response.customMessage()).containsIgnoringCase("yyyy-MM-ddTHH:mm:ssZ");
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
                              "mealTypeId": 1,
                              "isFreeMeal": false,
                              "standardOptionId": 1,
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
        void should_fail_with_boolean_instead_of_integer_for_mealTypeId() {
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "mealTypeId": true,
                              "isFreeMeal": false,
                              "standardOptionId": 1
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

        @Test
        void should_fail_with_string_instead_of_boolean_for_isFreeMeal() {
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "mealTypeId": 1,
                              "isFreeMeal": "not-a-boolean",
                              "standardOptionId": 1
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
                        assertThat(response.customMessage()).containsIgnoringCase("isFreeMeal");
                        assertThat(response.customMessage()).containsIgnoringCase("invalid format");
                    });
        }

        @Test
        void should_fail_with_string_instead_of_number_for_quantity() {
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "mealTypeId": 1,
                              "isFreeMeal": false,
                              "standardOptionId": 1,
                              "quantity": "not-a-number"
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
                        assertThat(response.customMessage()).containsIgnoringCase("quantity");
                        assertThat(response.customMessage()).containsIgnoringCase("invalid format");
                    });
        }
    }

    @Nested
    @DisplayName("GET /v1/meal-record/today - getTodayMealRecords")
    class GetTodayMealRecords {

        @Test
        void should_return_two_mealRecords() {
            mealRecordRepository.deleteAll();
            final MealTypeEntity mealType =
                    dietPlan.getMealTypes().stream().findFirst().orElseThrow();
            final StandardOptionEntity standardOption =
                    mealType.getStandardOptions().stream().findFirst().orElseThrow();

            final var requestPlannedMealRecord = DtoBuilders.buildCreateMealRecordRequestDtoPlannedMeal()
                    .mealTypeId(mealType.getId())
                    .standardOptionId(standardOption.getId())
                    .build();
            final var requestFreeMealRecord = DtoBuilders.buildCreateMealRecordRequestDtoFreeMeal()
                    .mealTypeId(mealType.getId())
                    .freeMealDescription("Pamonha")
                    .build();

            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestPlannedMealRecord)
                    .exchange()
                    .expectStatus()
                    .isCreated();
            webTestClient
                    .post()
                    .uri(getBaseUrl())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestFreeMealRecord)
                    .exchange()
                    .expectStatus()
                    .isCreated();
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl() + "/today")
                            .queryParam(
                                    RequestConstants.Query.TIMEZONE,
                                    ZoneId.systemDefault().getId())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBodyList(MealRecordResponseDto.class)
                    .value(response -> {
                        assertThat(response).isNotNull().hasSize(2);
                        assertThat(response)
                                .filteredOn(MealRecordResponseDto::isFreeMeal)
                                .first()
                                .extracting(MealRecordResponseDto::freeMealDescription)
                                .isEqualTo(requestFreeMealRecord.freeMealDescription());
                        assertThat(response)
                                .filteredOn(Predicate.not(MealRecordResponseDto::isFreeMeal))
                                .first()
                                .extracting(MealRecordResponseDto::standardOption)
                                .extracting(StandardOptionSimplifiedResponseDto::id)
                                .isEqualTo(requestPlannedMealRecord.standardOptionId());
                    });
        }

        @Test
        void should_return_empty_when_no_records_are_found() {
            mealRecordRepository.deleteAll();
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl() + "/today")
                            .queryParam(
                                    RequestConstants.Query.TIMEZONE,
                                    ZoneId.systemDefault().getId())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBodyList(MealRecordResponseDto.class)
                    .value(response -> assertThat(response).isNotNull().isEmpty());
        }

        @Test
        void should_fail_with_invalid_timezone() {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl() + "/today")
                            .queryParam(RequestConstants.Query.TIMEZONE, "Invalid/Timezone")
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.INVALID_VALUES.getCode());
                        assertThat(response.message()).isEqualTo(ExceptionCode.INVALID_VALUES.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(response.customMessage()).containsIgnoringCase("timezone");
                        assertThat(response.customMessage()).containsIgnoringCase("invalid");
                    });
        }

        @Test
        void should_fail_with_empty_timezone() {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl() + "/today")
                            .queryParam(RequestConstants.Query.TIMEZONE, "")
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.INVALID_VALUES.getCode());
                        assertThat(response.message()).isEqualTo(ExceptionCode.INVALID_VALUES.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(response.customMessage()).containsIgnoringCase("timezone");
                    });
        }
    }

    @Nested
    @DisplayName("GET /v1/meal-record - searchMealRecords")
    class SearchMealRecords {

        @Test
        void should_return_four_mealRecords() {
            mealRecordRepository.deleteAll();
            final MealTypeEntity mealType =
                    dietPlan.getMealTypes().stream().findFirst().orElseThrow();
            final StandardOptionEntity standardOption =
                    mealType.getStandardOptions().stream().findFirst().orElseThrow();

            final var requestPlannedMealRecord0 = DtoBuilders.buildCreateMealRecordRequestDtoPlannedMeal()
                    .mealTypeId(mealType.getId())
                    .standardOptionId(standardOption.getId())
                    .consumedAt(Instant.now().minus(3, ChronoUnit.DAYS))
                    .build();

            final var requestPlannedMealRecord1 = DtoBuilders.buildCreateMealRecordRequestDtoPlannedMeal()
                    .mealTypeId(mealType.getId())
                    .standardOptionId(standardOption.getId())
                    .build();
            final var requestPlannedMealRecord2 = DtoBuilders.buildCreateMealRecordRequestDtoPlannedMeal()
                    .mealTypeId(mealType.getId())
                    .standardOptionId(standardOption.getId())
                    .build();
            final var requestFreeMealRecord1 = DtoBuilders.buildCreateMealRecordRequestDtoFreeMeal()
                    .mealTypeId(mealType.getId())
                    .freeMealDescription("Pamonha")
                    .build();
            final var requestFreeMealRecord2 = DtoBuilders.buildCreateMealRecordRequestDtoFreeMeal()
                    .mealTypeId(mealType.getId())
                    .freeMealDescription("Pamonha")
                    .build();

            List.of(
                            requestPlannedMealRecord0,
                            requestPlannedMealRecord1,
                            requestPlannedMealRecord2,
                            requestFreeMealRecord1,
                            requestFreeMealRecord2)
                    .forEach(request -> webTestClient
                            .post()
                            .uri(getBaseUrl())
                            .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                            .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(request)
                            .exchange()
                            .expectStatus()
                            .isCreated());

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl())
                            .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                            .queryParam(
                                    RequestConstants.Query.CONSUMED_AT_AFTER,
                                    LocalDate.now()
                                            .atStartOfDay()
                                            .atZone(ZoneId.systemDefault())
                                            .toInstant())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(new ParameterizedTypeReference<PageResponseDto<MealRecordResponseDto>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalPages()).isEqualTo(1);
                        assertThat(response.getCurrentPage()).isEqualTo(0);
                        assertThat(response.getPageSize()).isNotNull();
                        assertThat(response.getTotalItems()).isEqualTo(4);
                        assertThat(response.getContent()).isNotNull().hasSize(4);
                    });
        }

        @Test
        void should_return_empty_page_when_no_records_are_found() {
            mealRecordRepository.deleteAll();
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl())
                            .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(new ParameterizedTypeReference<PageResponseDto<MealRecordResponseDto>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalPages()).isEqualTo(0);
                        assertThat(response.getCurrentPage()).isEqualTo(0);
                        assertThat(response.getPageSize()).isNotNull();
                        assertThat(response.getTotalItems()).isEqualTo(0);
                        assertThat(response.getContent()).isNotNull().hasSize(0);
                    });
        }

        @Test
        void should_fail_with_invalid_consumedAtAfter_format() {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl())
                            .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                            .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, "2025-11-14T10:34:55")
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.INVALID_VALUES.getCode());
                        assertThat(response.message()).isEqualTo(ExceptionCode.INVALID_VALUES.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(response.customMessage()).containsIgnoringCase("consumedAtAfter");
                        assertThat(response.customMessage()).containsIgnoringCase("yyyy-MM-ddTHH:mm:ssZ");
                    });
        }

        @Test
        void should_fail_with_invalid_consumedAtBefore_format() {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl())
                            .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                            .queryParam(RequestConstants.Query.CONSUMED_AT_BEFORE, "invalid-date")
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.INVALID_VALUES.getCode());
                        assertThat(response.message()).isEqualTo(ExceptionCode.INVALID_VALUES.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(response.customMessage()).containsIgnoringCase("consumedAtBefore");
                        assertThat(response.customMessage()).containsIgnoringCase("errorMotive");
                    });
        }

        @Test
        void should_fail_with_string_instead_of_integer_for_pageNumber() {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl())
                            .queryParam(RequestConstants.Query.PAGE_NUMBER, "not-a-number")
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.INVALID_VALUES.getCode());
                        assertThat(response.message()).isEqualTo(ExceptionCode.INVALID_VALUES.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(response.customMessage()).containsIgnoringCase("pageNumber");
                    });
        }

        @Test
        void should_fail_with_string_instead_of_boolean_for_isFreeMeal() {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl())
                            .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                            .queryParam(RequestConstants.Query.IS_FREE_MEAL, "not-a-boolean")
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.INVALID_VALUES.getCode());
                        assertThat(response.message()).isEqualTo(ExceptionCode.INVALID_VALUES.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(response.customMessage()).containsIgnoringCase("isFreeMeal");
                    });
        }
    }

    @Nested
    @DisplayName("GET /v1/meal-record/statistics - getMealRecordStatisticsByDateRange")
    class GetMealRecordStatistics {

        @Test
        void should_successfully_return_statistics() {
            mealRecordRepository.deleteAll();
            final MealTypeEntity mealType =
                    dietPlan.getMealTypes().stream().findFirst().orElseThrow();
            final StandardOptionEntity standardOption =
                    mealType.getStandardOptions().stream().findFirst().orElseThrow();

            final var requestPlannedMealRecord0 = DtoBuilders.buildCreateMealRecordRequestDtoPlannedMeal()
                    .mealTypeId(mealType.getId())
                    .standardOptionId(standardOption.getId())
                    .consumedAt(Instant.now().minus(3, ChronoUnit.DAYS))
                    .build();

            final var requestPlannedMealRecord1 = DtoBuilders.buildCreateMealRecordRequestDtoPlannedMeal()
                    .mealTypeId(mealType.getId())
                    .standardOptionId(standardOption.getId())
                    .build();
            final var requestPlannedMealRecord2 = DtoBuilders.buildCreateMealRecordRequestDtoPlannedMeal()
                    .mealTypeId(mealType.getId())
                    .standardOptionId(standardOption.getId())
                    .build();
            final var requestFreeMealRecord1 = DtoBuilders.buildCreateMealRecordRequestDtoFreeMeal()
                    .mealTypeId(mealType.getId())
                    .freeMealDescription("Pamonha")
                    .build();
            final var requestFreeMealRecord2 = DtoBuilders.buildCreateMealRecordRequestDtoFreeMeal()
                    .mealTypeId(mealType.getId())
                    .freeMealDescription("Pamonha")
                    .build();

            List.of(
                            requestPlannedMealRecord0,
                            requestPlannedMealRecord1,
                            requestPlannedMealRecord2,
                            requestFreeMealRecord1,
                            requestFreeMealRecord2)
                    .forEach(request -> webTestClient
                            .post()
                            .uri(getBaseUrl())
                            .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                            .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(request)
                            .exchange()
                            .expectStatus()
                            .isCreated());

            final var currentDate = LocalDate.now();

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl())
                            .pathSegment("statistics")
                            .queryParam(
                                    RequestConstants.Query.CONSUMED_AT_AFTER,
                                    currentDate
                                            .atStartOfDay()
                                            .atZone(ZoneId.systemDefault())
                                            .toInstant())
                            .queryParam(
                                    RequestConstants.Query.CONSUMED_AT_BEFORE,
                                    currentDate
                                            .atTime(LocalTime.MAX)
                                            .atZone(ZoneId.systemDefault())
                                            .toInstant())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(MealRecordStatisticsResponseDto.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.freeMealQuantity()).isEqualTo(2);
                        assertThat(response.plannedMealQuantity()).isEqualTo(2);
                        assertThat(response.mealQuantity()).isEqualTo(4);
                    });
        }

        @Test
        void should_return_empty_when_no_record_are_registered() {
            mealRecordRepository.deleteAll();
            final var currentDate = LocalDate.now();

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl())
                            .pathSegment("statistics")
                            .queryParam(
                                    RequestConstants.Query.CONSUMED_AT_AFTER,
                                    currentDate
                                            .atStartOfDay()
                                            .atZone(ZoneId.systemDefault())
                                            .toInstant())
                            .queryParam(
                                    RequestConstants.Query.CONSUMED_AT_BEFORE,
                                    currentDate
                                            .atTime(LocalTime.MAX)
                                            .atZone(ZoneId.systemDefault())
                                            .toInstant())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(MealRecordStatisticsResponseDto.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.freeMealQuantity()).isEqualTo(0);
                        assertThat(response.plannedMealQuantity()).isEqualTo(0);
                        assertThat(response.mealQuantity()).isEqualTo(0);
                    });
        }

        @Test
        void should_fail_with_invalid_consumedAtAfter_format() {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getBaseUrl())
                            .pathSegment("statistics")
                            .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, "2025-99-99T99:99:99Z")
                            .queryParam(RequestConstants.Query.CONSUMED_AT_BEFORE, Instant.now())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(response -> {
                        assertThat(response.code()).isEqualTo(ExceptionCode.INVALID_VALUES.getCode());
                        assertThat(response.message()).isEqualTo(ExceptionCode.INVALID_VALUES.getMessage());
                        assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(response.customMessage()).containsIgnoringCase("consumedAtAfter");
                        assertThat(response.customMessage()).containsIgnoringCase("errorMotive");
                    });
        }
    }

    @Nested
    @DisplayName("Required headers validation")
    class RequiredHeaders {

        @Test
        void should_fail_when_missing_required_headers() {
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
                    .value(msg -> assertThat(msg.toString()).containsIgnoringCase(headerNameXCorrelationId));
        }

        @Test
        void should_fail_when_missing_xChannel() {
            final String headerNameXCorrelationId = RequestConstants.Headers.X_CORRELATION_ID;
            final String headerNameXChannel = RequestConstants.Headers.X_CHANNEL;

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
                    .value(msg -> assertThat(msg.toString()).containsIgnoringCase(headerNameXChannel));
        }
    }
}
