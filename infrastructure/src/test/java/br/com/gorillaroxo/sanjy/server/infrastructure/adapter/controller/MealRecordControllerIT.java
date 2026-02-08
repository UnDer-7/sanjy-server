package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.gorillaroxo.sanjy.server.core.exception.ExceptionCode;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordCreatedResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.PageResponseDto;
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
import java.time.format.DateTimeFormatter;
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

    static final String BASE_URL = "/v1/meal-record";

    DietPlanEntity dietPlan;

    @BeforeAll
    void setup() {
        cleanUpDatabase();
        dietPlan = createDietPlan();
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
                    .uri(BASE_URL)
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
                        assertThat(response.quantity()).isEqualByComparingTo(request.quantity());
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
                    .uri(BASE_URL)
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
                    .uri(BASE_URL)
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
                    .uri(BASE_URL)
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
                    .uri(BASE_URL)
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
                    .uri(BASE_URL)
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
                    .uri(BASE_URL)
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
                    .uri(BASE_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestPlannedMealRecord)
                    .exchange()
                    .expectStatus()
                    .isCreated();
            webTestClient
                    .post()
                    .uri(BASE_URL)
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
                            .path(BASE_URL + "/today")
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

                        // Validate PlannedMealRecord (isFreeMeal = false)
                        final var plannedMealRecord = response.stream()
                                .filter(Predicate.not(MealRecordResponseDto::isFreeMeal))
                                .findFirst()
                                .orElseThrow();

                        assertThat(plannedMealRecord.id()).isNotNull();
                        assertThat(plannedMealRecord.consumedAt()).isNotNull();
                        assertThat(plannedMealRecord.isFreeMeal()).isFalse();
                        assertThat(plannedMealRecord.freeMealDescription()).isNull();
                        assertThat(plannedMealRecord.quantity())
                                .isEqualByComparingTo(requestPlannedMealRecord.quantity());
                        assertThat(plannedMealRecord.unit()).isEqualTo(requestPlannedMealRecord.unit());
                        assertThat(plannedMealRecord.notes()).isEqualTo(requestPlannedMealRecord.notes());

                        assertThat(plannedMealRecord.metadata()).isNotNull();
                        assertThat(plannedMealRecord.metadata().createdAt()).isNotNull();
                        assertThat(plannedMealRecord.metadata().updatedAt()).isNotNull();

                        final var timeFormatter =
                                DateTimeFormatter.ofPattern(RequestConstants.DateTimeFormats.TIME_FORMAT);

                        assertThat(plannedMealRecord.mealType()).isNotNull();
                        assertThat(plannedMealRecord.mealType().id()).isEqualTo(mealType.getId());
                        assertThat(plannedMealRecord.mealType().name()).isEqualTo(mealType.getName());
                        assertThat(plannedMealRecord.mealType().scheduledTime().format(timeFormatter))
                                .isEqualTo(mealType.getScheduledTime().format(timeFormatter));
                        assertThat(plannedMealRecord.mealType().observation()).isEqualTo(mealType.getObservation());
                        assertThat(plannedMealRecord.mealType().metadata()).isNotNull();
                        assertThat(plannedMealRecord.mealType().metadata().createdAt())
                                .isNotNull();
                        assertThat(plannedMealRecord.mealType().metadata().updatedAt())
                                .isNotNull();

                        assertThat(plannedMealRecord.standardOption()).isNotNull();
                        assertThat(plannedMealRecord.standardOption().id()).isEqualTo(standardOption.getId());
                        assertThat(plannedMealRecord
                                        .standardOption()
                                        .optionNumber()
                                        .intValue())
                                .isEqualTo(standardOption.getOptionNumber());
                        assertThat(plannedMealRecord.standardOption().description())
                                .isEqualTo(standardOption.getDescription());
                        assertThat(plannedMealRecord.standardOption().metadata())
                                .isNotNull();
                        assertThat(plannedMealRecord.standardOption().metadata().createdAt())
                                .isNotNull();
                        assertThat(plannedMealRecord.standardOption().metadata().updatedAt())
                                .isNotNull();

                        // Validate FreeMealRecord (isFreeMeal = true)
                        final var freeMealRecord = response.stream()
                                .filter(MealRecordResponseDto::isFreeMeal)
                                .findFirst()
                                .orElseThrow();

                        assertThat(freeMealRecord.id()).isNotNull();
                        assertThat(freeMealRecord.consumedAt()).isNotNull();
                        assertThat(freeMealRecord.isFreeMeal()).isTrue();
                        assertThat(freeMealRecord.freeMealDescription())
                                .isEqualTo(requestFreeMealRecord.freeMealDescription());
                        assertThat(freeMealRecord.standardOption()).isNull();
                        assertThat(freeMealRecord.quantity()).isEqualByComparingTo(requestFreeMealRecord.quantity());
                        assertThat(freeMealRecord.unit()).isEqualTo(requestFreeMealRecord.unit());
                        assertThat(freeMealRecord.notes()).isEqualTo(requestFreeMealRecord.notes());

                        assertThat(freeMealRecord.metadata()).isNotNull();
                        assertThat(freeMealRecord.metadata().createdAt()).isNotNull();
                        assertThat(freeMealRecord.metadata().updatedAt()).isNotNull();

                        assertThat(freeMealRecord.mealType()).isNotNull();
                        assertThat(freeMealRecord.mealType().id()).isEqualTo(mealType.getId());
                        assertThat(freeMealRecord.mealType().name()).isEqualTo(mealType.getName());
                        assertThat(freeMealRecord.mealType().scheduledTime().format(timeFormatter))
                                .isEqualTo(mealType.getScheduledTime().format(timeFormatter));
                        assertThat(freeMealRecord.mealType().observation()).isEqualTo(mealType.getObservation());
                        assertThat(freeMealRecord.mealType().metadata()).isNotNull();
                        assertThat(freeMealRecord.mealType().metadata().createdAt())
                                .isNotNull();
                        assertThat(freeMealRecord.mealType().metadata().updatedAt())
                                .isNotNull();
                    });
        }

        @Test
        void should_return_empty_when_no_records_are_found() {
            mealRecordRepository.deleteAll();
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(BASE_URL + "/today")
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
                            .path(BASE_URL + "/today")
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
                            .path(BASE_URL + "/today")
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
                    .freeMealDescription("Canjica")
                    .build();

            List.of(
                            requestPlannedMealRecord0,
                            requestPlannedMealRecord1,
                            requestPlannedMealRecord2,
                            requestFreeMealRecord1,
                            requestFreeMealRecord2)
                    .forEach(request -> webTestClient
                            .post()
                            .uri(BASE_URL)
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
                            .path(BASE_URL)
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
                        // Validate pagination data
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalPages()).isEqualTo(1);
                        assertThat(response.getCurrentPage()).isZero();
                        assertThat(response.getPageSize()).isNotNull().isPositive();
                        assertThat(response.getTotalItems()).isEqualTo(4);
                        assertThat(response.getContent()).isNotNull().hasSize(4);

                        final var timeFormatter =
                                DateTimeFormatter.ofPattern(RequestConstants.DateTimeFormats.TIME_FORMAT);

                        // Validate PlannedMealRecord (isFreeMeal = false)
                        final var plannedMealRecord = response.getContent().stream()
                                .filter(Predicate.not(MealRecordResponseDto::isFreeMeal))
                                .findFirst()
                                .orElseThrow();

                        assertThat(plannedMealRecord.id()).isNotNull();
                        assertThat(plannedMealRecord.consumedAt()).isNotNull();
                        assertThat(plannedMealRecord.isFreeMeal()).isFalse();
                        assertThat(plannedMealRecord.freeMealDescription()).isNull();
                        assertThat(plannedMealRecord.quantity())
                                .isEqualByComparingTo(requestPlannedMealRecord1.quantity());
                        assertThat(plannedMealRecord.unit()).isEqualTo(requestPlannedMealRecord1.unit());
                        assertThat(plannedMealRecord.notes()).isEqualTo(requestPlannedMealRecord1.notes());

                        assertThat(plannedMealRecord.metadata()).isNotNull();
                        assertThat(plannedMealRecord.metadata().createdAt()).isNotNull();
                        assertThat(plannedMealRecord.metadata().updatedAt()).isNotNull();

                        assertThat(plannedMealRecord.mealType()).isNotNull();
                        assertThat(plannedMealRecord.mealType().id()).isEqualTo(mealType.getId());
                        assertThat(plannedMealRecord.mealType().name()).isEqualTo(mealType.getName());
                        assertThat(plannedMealRecord.mealType().scheduledTime().format(timeFormatter))
                                .isEqualTo(mealType.getScheduledTime().format(timeFormatter));
                        assertThat(plannedMealRecord.mealType().observation()).isEqualTo(mealType.getObservation());
                        assertThat(plannedMealRecord.mealType().metadata()).isNotNull();
                        assertThat(plannedMealRecord.mealType().metadata().createdAt())
                                .isNotNull();
                        assertThat(plannedMealRecord.mealType().metadata().updatedAt())
                                .isNotNull();

                        assertThat(plannedMealRecord.standardOption()).isNotNull();
                        assertThat(plannedMealRecord.standardOption().id()).isEqualTo(standardOption.getId());
                        assertThat(plannedMealRecord.standardOption().optionNumber())
                                .isEqualTo(standardOption.getOptionNumber().longValue());
                        assertThat(plannedMealRecord.standardOption().description())
                                .isEqualTo(standardOption.getDescription());
                        assertThat(plannedMealRecord.standardOption().metadata())
                                .isNotNull();
                        assertThat(plannedMealRecord.standardOption().metadata().createdAt())
                                .isNotNull();
                        assertThat(plannedMealRecord.standardOption().metadata().updatedAt())
                                .isNotNull();

                        // Validate FreeMealRecord (isFreeMeal = true)
                        final var freeMealRecord = response.getContent().stream()
                                .filter(MealRecordResponseDto::isFreeMeal)
                                .findFirst()
                                .orElseThrow();

                        assertThat(freeMealRecord.id()).isNotNull();
                        assertThat(freeMealRecord.consumedAt()).isNotNull();
                        assertThat(freeMealRecord.isFreeMeal()).isTrue();
                        assertThat(freeMealRecord.freeMealDescription()).isNotNull();
                        assertThat(freeMealRecord.standardOption()).isNull();
                        assertThat(freeMealRecord.quantity()).isNotNull();
                        assertThat(freeMealRecord.unit()).isNotNull();
                        assertThat(freeMealRecord.notes()).isEqualTo(requestFreeMealRecord1.notes());

                        assertThat(freeMealRecord.metadata()).isNotNull();
                        assertThat(freeMealRecord.metadata().createdAt()).isNotNull();
                        assertThat(freeMealRecord.metadata().updatedAt()).isNotNull();

                        assertThat(freeMealRecord.mealType()).isNotNull();
                        assertThat(freeMealRecord.mealType().id()).isEqualTo(mealType.getId());
                        assertThat(freeMealRecord.mealType().name()).isEqualTo(mealType.getName());
                        assertThat(freeMealRecord.mealType().scheduledTime().format(timeFormatter))
                                .isEqualTo(mealType.getScheduledTime().format(timeFormatter));
                        assertThat(freeMealRecord.mealType().observation()).isEqualTo(mealType.getObservation());
                        assertThat(freeMealRecord.mealType().metadata()).isNotNull();
                        assertThat(freeMealRecord.mealType().metadata().createdAt())
                                .isNotNull();
                        assertThat(freeMealRecord.mealType().metadata().updatedAt())
                                .isNotNull();

                        // Validate counts
                        final long plannedMealCount = response.getContent().stream()
                                .filter(Predicate.not(MealRecordResponseDto::isFreeMeal))
                                .count();
                        final long freeMealCount = response.getContent().stream()
                                .filter(MealRecordResponseDto::isFreeMeal)
                                .count();
                        assertThat(plannedMealCount).isEqualTo(2);
                        assertThat(freeMealCount).isEqualTo(2);
                    });
        }

        @Test
        void should_return_empty_page_when_no_records_are_found() {
            mealRecordRepository.deleteAll();
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(BASE_URL)
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
                        assertThat(response.getTotalPages()).isZero();
                        assertThat(response.getCurrentPage()).isZero();
                        assertThat(response.getPageSize()).isNotNull();
                        assertThat(response.getTotalItems()).isZero();
                        assertThat(response.getContent()).isNotNull().isEmpty();
                    });
        }

        @Test
        void should_fail_with_invalid_consumedAtAfter_format() {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(BASE_URL)
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
                            .path(BASE_URL)
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
                            .path(BASE_URL)
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
                            .path(BASE_URL)
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
                            .uri(BASE_URL)
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
                            .path(BASE_URL)
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
                            .path(BASE_URL)
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
                        assertThat(response.freeMealQuantity()).isZero();
                        assertThat(response.plannedMealQuantity()).isZero();
                        assertThat(response.mealQuantity()).isZero();
                    });
        }

        @Test
        void should_fail_with_invalid_consumedAtAfter_format() {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(BASE_URL)
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
                    .uri(BASE_URL)
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
                    .value(msg -> assertThat(msg.toString())
                            .containsIgnoringCase(headerNameXCorrelationId)
                            .containsIgnoringCase("valid UUID format"));
        }

        @Test
        void should_fail_when_only_passing_invalid_correlation_id() {
            final String headerNameXCorrelationId = RequestConstants.Headers.X_CORRELATION_ID;

            webTestClient
                    .post()
                    .uri(BASE_URL)
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
                    .value(msg -> assertThat(msg.toString()).containsIgnoringCase(headerNameXChannel));
        }
    }
}
