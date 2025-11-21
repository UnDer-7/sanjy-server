package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.IntegrationTestController;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

class DietPlanControllerIT extends IntegrationTestController {

    private static final String BASE_URL = "/v1/diet-plan";

    @Nested
    @DisplayName("Test default required headers")
    class InvalidHeaders {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        void should_fail_when_passing_invalid_values(final String correlationId) {

            final RequestSpecification given = given();

            final String headerNameXCorrelationId = RequestConstants.Headers.X_CORRELATION_ID;
            final String headerNameXChannel = RequestConstants.Headers.X_CHANNEL;

            if (correlationId != null) {
                given.header(headerNameXCorrelationId, correlationId);
                given.header(headerNameXChannel, correlationId);
            }

            given.when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("customMessage", containsStringIgnoringCase(headerNameXCorrelationId))
                    .body("customMessage", containsStringIgnoringCase(headerNameXChannel));
        }

        @Test
        void should_fail_when_passing_invalid_uuid_correlation_id() {
            final String headerNameXCorrelationId = RequestConstants.Headers.X_CORRELATION_ID;

            given().header(headerNameXCorrelationId, "7d1c9e48034744eca14256e77fc11dfe")
                    .header(RequestConstants.Headers.X_CHANNEL, "integration-test")
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("customMessage", containsStringIgnoringCase(headerNameXCorrelationId))
                    .body("customMessage", containsStringIgnoringCase("valid UUID format"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        void should_fail_when_only_passing_invalid_correlation_id(final String correlationId) {
            final String headerNameXCorrelationId = RequestConstants.Headers.X_CORRELATION_ID;
            final String headerNameXChannel = RequestConstants.Headers.X_CHANNEL;

            given().header(headerNameXCorrelationId, correlationId)
                    .header(headerNameXChannel, "integration-test")
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("customMessage", containsStringIgnoringCase(headerNameXCorrelationId))
                    .body("customMessage", not(containsStringIgnoringCase(headerNameXChannel)));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        void should_fail_when_only_passing_invalid_xChannel(final String xChannel) {
            final String headerNameXCorrelationId = RequestConstants.Headers.X_CORRELATION_ID;
            final String headerNameXChannel = RequestConstants.Headers.X_CHANNEL;

            given().header(headerNameXCorrelationId, "7f804519-7a52-485c-983d-19439e5cc7a3")
                    .header(headerNameXChannel, xChannel)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("customMessage", containsStringIgnoringCase(headerNameXChannel))
                    .body("customMessage", not(containsStringIgnoringCase(headerNameXCorrelationId)));
        }
    }
}
