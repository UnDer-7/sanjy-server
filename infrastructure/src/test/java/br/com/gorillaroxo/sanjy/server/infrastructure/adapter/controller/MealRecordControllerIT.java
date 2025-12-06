package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.gorillaroxo.sanjy.server.core.exception.ExceptionCode;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.IdOnlyResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.PageResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.StandardOptionResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.DietPlanEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.MealTypeEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.StandardOptionEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.builder.DtoBuilders;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Test
    void newMealRecord__should_successfully_create_planned_mealRecord() {
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
                .expectBody(MealRecordResponseDto.class)
                .value(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.consumedAt()).isNotNull();
                    assertThat(response.mealType().id()).isEqualTo(mealType.getId());
                    assertThat(response.isFreeMeal()).isEqualTo(request.isFreeMeal());
                    assertThat(response.standardOption().id()).isEqualTo(standardOption.getId());
                    assertThat(response.freeMealDescription()).isEqualTo(request.freeMealDescription());
                    assertThat(response.quantity()).isEqualTo(request.quantity());
                    assertThat(response.unit()).isEqualTo(request.unit());
                    assertThat(response.notes()).isEqualTo(request.notes());
                    assertThat(response.createdAt()).isNotNull();
                });
    }

    @Test
    void newMealRecord__should_fail_when_passing_null_mealType_id() {
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
                    assertThat(response.timestamp()).isNotBlank();
                    assertThat(response.message()).isNotEmpty().isEqualTo(expectedExCode.getMessage());
                    assertThat(response.customMessage()).isNotEmpty().containsIgnoringCase("mealTypeId");
                    assertThat(response.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
    }

    @Test
    void getTodayMealRecords__should_return_two_mealRecords() {
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
                .uri(getBaseUrl() + "/today")
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
                            .extracting(IdOnlyResponseDto::id)
                            .isEqualTo(requestPlannedMealRecord.standardOptionId());
                });
    }

    @Test
    void getTodayMealRecords__should_empty_when_no_records_are_found() {
        mealRecordRepository.deleteAll();
        webTestClient
                .get()
                .uri(getBaseUrl() + "/today")
                .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(MealRecordResponseDto.class)
                .value(response -> {
                    assertThat(response).isNotNull().isEmpty();
                });
    }

    @Test
    void searchMealRecords__should_return_four_mealRecords() {
        mealRecordRepository.deleteAll();
        final MealTypeEntity mealType =
                dietPlan.getMealTypes().stream().findFirst().orElseThrow();
        final StandardOptionEntity standardOption =
                mealType.getStandardOptions().stream().findFirst().orElseThrow();

        final var requestPlannedMealRecord0 = DtoBuilders.buildCreateMealRecordRequestDtoPlannedMeal()
                .mealTypeId(mealType.getId())
                .standardOptionId(standardOption.getId())
                .consumedAt(LocalDateTime.now().minusDays(3))
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
                                LocalDate.now().atStartOfDay())
                        .build())
                .header(RequestConstants.Headers.X_CORRELATION_ID, "bf5ef8a2-5af2-4adf-8b58-d186fe01cd11")
                .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<PageResponseDto<MealRecordResponseDto>>() {})
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.totalPages()).isEqualTo(1);
                    assertThat(response.currentPage()).isEqualTo(0);
                    assertThat(response.pageSize()).isNotNull();
                    assertThat(response.totalItems()).isEqualTo(4);
                    assertThat(response.content()).isNotNull().hasSize(4);
                });
    }

    @Test
    void searchMealRecords__should_return_empty_page_when_no_records_are_found() {
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
                    assertThat(response.totalPages()).isEqualTo(0);
                    assertThat(response.currentPage()).isEqualTo(0);
                    assertThat(response.pageSize()).isNotNull();
                    assertThat(response.totalItems()).isEqualTo(0);
                    assertThat(response.content()).isNotNull().hasSize(0);
                });
    }

    @Test
    void getMealRecordStatisticsByDateRange__should_successfully_return_statistics() {
        mealRecordRepository.deleteAll();
        final MealTypeEntity mealType =
                dietPlan.getMealTypes().stream().findFirst().orElseThrow();
        final StandardOptionEntity standardOption =
                mealType.getStandardOptions().stream().findFirst().orElseThrow();

        final var requestPlannedMealRecord0 = DtoBuilders.buildCreateMealRecordRequestDtoPlannedMeal()
                .mealTypeId(mealType.getId())
                .standardOptionId(standardOption.getId())
                .consumedAt(LocalDateTime.now().minusDays(3))
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
                        .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, currentDate.atStartOfDay())
                        .queryParam(
                                RequestConstants.Query.CONSUMED_AT_BEFORE, LocalDateTime.of(currentDate, LocalTime.MAX))
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
    void getMealRecordStatisticsByDateRange__should_empty_when_no_record_are_registered() {
        mealRecordRepository.deleteAll();
        final var currentDate = LocalDate.now();

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(getBaseUrl())
                        .pathSegment("statistics")
                        .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, currentDate.atStartOfDay())
                        .queryParam(
                                RequestConstants.Query.CONSUMED_AT_BEFORE, LocalDateTime.of(currentDate, LocalTime.MAX))
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
