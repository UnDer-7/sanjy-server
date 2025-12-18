package br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.cofig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.gorillaroxo.sanjy.server.core.exception.BusinessException;
import br.com.gorillaroxo.sanjy.server.core.exception.DietPlanNotFoundException;
import br.com.gorillaroxo.sanjy.server.core.exception.UnhandledClientHttpException;
import br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.cofig.handler.FeignErrorHandler;
import feign.Request;
import feign.Request.HttpMethod;
import feign.Response;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeignErrorDecoderTest {

    @Test
    void should_use_error_handler_when_handler_can_handle_error() {
        // Given
        final var errorHandler = mock(FeignErrorHandler.class);
        final var businessException = mock(DietPlanNotFoundException.class);

        final var response = createMockResponse(400, "Error response body");

        when(errorHandler.canHandle(response, "Error response body")).thenReturn(true);
        when(errorHandler.handle(response, "Error response body")).thenReturn(businessException);

        final var decoder = new FeignErrorDecoder(Set.of(errorHandler));

        // When
        final Exception result = decoder.decode("TestMethod", response);

        // Then
        assertThat(result).isSameAs(businessException);
    }

    @Test
    void should_return_unhandled_exception_when_no_handler_can_handle_error() {
        // Given
        final var errorHandler = mock(FeignErrorHandler.class);
        final var response = createMockResponse(500, "Internal server error");

        when(errorHandler.canHandle(response, "Internal server error")).thenReturn(false);

        final var decoder = new FeignErrorDecoder(Set.of(errorHandler));

        // When
        final Exception result = decoder.decode("TestMethod#testEndpoint()", response);

        // Then
        assertThat(result)
                .isInstanceOf(UnhandledClientHttpException.class)
                .extracting(ex -> ((UnhandledClientHttpException) ex).getRequestInformation())
                .satisfies(info -> {
                    assertThat(info.getFeignMethodKey()).contains("TestMethod#testEndpoint()");
                    assertThat(info.getHttpStatusCode()).contains(500);
                    assertThat(info.getRequestMethod()).contains("GET");
                    assertThat(info.getRequestUrl()).contains("http://test.com/api");
                    assertThat(info.getResponseBody()).contains("Internal server error");
                });
    }

    @Test
    void should_return_unhandled_exception_when_no_handlers_provided() {
        // Given
        final var response = createMockResponse(404, "Not found");
        final var decoder = new FeignErrorDecoder(Set.of());

        // When
        final Exception result = decoder.decode("TestMethod", response);

        // Then
        assertThat(result).isInstanceOf(UnhandledClientHttpException.class);
    }

    @Test
    void should_use_first_matching_handler_when_multiple_handlers_can_handle() {
        // Given
        final var handler1 = mock(FeignErrorHandler.class);
        final var handler2 = mock(FeignErrorHandler.class);
        final var exception1 = mock(BusinessException.class);

        final var response = createMockResponse(400, "Bad request");

        when(handler1.canHandle(response, "Bad request")).thenReturn(true);
        when(handler1.handle(response, "Bad request")).thenReturn(exception1);

        final var decoder = new FeignErrorDecoder(Set.of(handler1, handler2));

        // When
        final Exception result = decoder.decode("TestMethod", response);

        // Then
        assertThat(result).isSameAs(exception1);
    }

    @Test
    void should_handle_null_response_gracefully() {
        // Given
        final var errorHandler = mock(FeignErrorHandler.class);
        when(errorHandler.canHandle(null, null)).thenReturn(false);

        final var decoder = new FeignErrorDecoder(Set.of(errorHandler));

        // When
        final Exception result = decoder.decode("TestMethod", null);

        // Then
        assertThat(result)
                .isInstanceOf(UnhandledClientHttpException.class)
                .extracting(ex -> ((UnhandledClientHttpException) ex).getRequestInformation())
                .satisfies(info -> {
                    assertThat(info.getHttpStatusCode()).isEmpty();
                    assertThat(info.getRequestMethod()).isEmpty();
                    assertThat(info.getRequestUrl()).isEmpty();
                    assertThat(info.getResponseBody()).isEmpty();
                });
    }

    @Test
    void should_handle_response_with_null_body_gracefully() {
        // Given
        final var response = createMockResponseWithNullBody(500);
        final var errorHandler = mock(FeignErrorHandler.class);
        when(errorHandler.canHandle(response, null)).thenReturn(false);

        final var decoder = new FeignErrorDecoder(Set.of(errorHandler));

        // When
        final Exception result = decoder.decode("TestMethod", response);

        // Then
        assertThat(result)
                .isInstanceOf(UnhandledClientHttpException.class)
                .extracting(ex -> ((UnhandledClientHttpException) ex).getRequestInformation())
                .satisfies(info -> {
                    assertThat(info.getHttpStatusCode()).contains(500);
                    assertThat(info.getResponseBody()).isEmpty();
                });
    }

    @Test
    void should_extract_request_headers_from_response() {
        // Given
        final Map<String, Collection<String>> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", List.of("Bearer token123"));
        requestHeaders.put("Content-Type", List.of("application/json"));

        final var response = createMockResponseWithHeaders(400, "Error", requestHeaders);
        final var errorHandler = mock(FeignErrorHandler.class);
        when(errorHandler.canHandle(response, "Error")).thenReturn(false);

        final var decoder = new FeignErrorDecoder(Set.of(errorHandler));

        // When
        final Exception result = decoder.decode("TestMethod", response);

        // Then
        assertThat(result)
                .isInstanceOf(UnhandledClientHttpException.class)
                .extracting(ex -> ((UnhandledClientHttpException) ex).getRequestInformation())
                .satisfies(info -> {
                    assertThat(info.getRequestHeaders()).isNotNull().containsKeys("Authorization", "Content-Type");
                });
    }

    @Test
    void should_extract_response_headers_from_response() {
        // Given
        final Map<String, Collection<String>> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", List.of("application/json"));
        responseHeaders.put("X-Custom-Header", List.of("custom-value"));

        final var response = createMockResponseWithResponseHeaders(500, "Error", responseHeaders);
        final var errorHandler = mock(FeignErrorHandler.class);
        when(errorHandler.canHandle(response, "Error")).thenReturn(false);

        final var decoder = new FeignErrorDecoder(Set.of(errorHandler));

        // When
        final Exception result = decoder.decode("TestMethod", response);

        // Then
        assertThat(result)
                .isInstanceOf(UnhandledClientHttpException.class)
                .extracting(ex -> ((UnhandledClientHttpException) ex).getRequestInformation())
                .satisfies(info -> {
                    assertThat(info.getResponseHeaders())
                            .isNotNull()
                            .containsKeys("Content-Type", "X-Custom-Header");
                });
    }

    @Test
    void should_handle_response_body_read_error_gracefully() {
        // Given
        final var response = createMockResponseWithBrokenBody(400);
        final var errorHandler = mock(FeignErrorHandler.class);
        when(errorHandler.canHandle(response, null)).thenReturn(false);

        final var decoder = new FeignErrorDecoder(Set.of(errorHandler));

        // When
        final Exception result = decoder.decode("TestMethod", response);

        // Then
        assertThat(result)
                .isInstanceOf(UnhandledClientHttpException.class)
                .extracting(ex -> ((UnhandledClientHttpException) ex).getRequestInformation())
                .satisfies(info -> assertThat(info.getResponseBody()).isEmpty());
    }

    private Response createMockResponse(final int status, final String body) {
        final var request = Request.create(HttpMethod.GET, "http://test.com/api", Map.of(), null, Charset.defaultCharset(), null);

        final var responseBody = mock(Response.Body.class);
        try {
            when(responseBody.asReader(Charset.defaultCharset()))
                    .thenReturn(new java.io.StringReader(body));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return Response.builder()
                .status(status)
                .reason("Test reason")
                .headers(Map.of())
                .request(request)
                .body(responseBody)
                .build();
    }

    private Response createMockResponseWithNullBody(final int status) {
        final var request = Request.create(HttpMethod.GET, "http://test.com/api", Map.of(), null, Charset.defaultCharset(), null);

        return Response.builder()
                .status(status)
                .reason("Test reason")
                .headers(Map.of())
                .request(request)
                .build();
    }

    private Response createMockResponseWithHeaders(
            final int status, final String body, final Map<String, Collection<String>> requestHeaders) {
        final var request = Request.create(HttpMethod.POST, "http://test.com/api", requestHeaders, null, Charset.defaultCharset(), null);

        final var responseBody = mock(Response.Body.class);
        try {
            when(responseBody.asReader(Charset.defaultCharset()))
                    .thenReturn(new java.io.StringReader(body));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return Response.builder()
                .status(status)
                .reason("Test reason")
                .headers(Map.of())
                .request(request)
                .body(responseBody)
                .build();
    }

    private Response createMockResponseWithResponseHeaders(
            final int status, final String body, final Map<String, Collection<String>> responseHeaders) {
        final var request = Request.create(HttpMethod.GET, "http://test.com/api", Map.of(), null, Charset.defaultCharset(), null);

        final var responseBody = mock(Response.Body.class);
        try {
            when(responseBody.asReader(Charset.defaultCharset()))
                    .thenReturn(new java.io.StringReader(body));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return Response.builder()
                .status(status)
                .reason("Test reason")
                .headers(responseHeaders)
                .request(request)
                .body(responseBody)
                .build();
    }

    private Response createMockResponseWithBrokenBody(final int status) {
        final var request = Request.create(HttpMethod.GET, "http://test.com/api", Map.of(), null, Charset.defaultCharset(), null);

        final var brokenBody = mock(Response.Body.class);
        try {
            when(brokenBody.asReader(Charset.defaultCharset())).thenAnswer(invocation -> {
                return new Reader() {
                    @Override
                    public int read(final char[] cbuf, final int off, final int len) throws IOException {
                        throw new IOException("Broken stream");
                    }

                    @Override
                    public void close() {}
                };
            });
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return Response.builder()
                .status(status)
                .reason("Test reason")
                .headers(Map.of())
                .request(request)
                .body(brokenBody)
                .build();
    }
}
