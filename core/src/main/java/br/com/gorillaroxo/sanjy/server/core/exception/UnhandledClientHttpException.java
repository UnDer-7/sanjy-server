package br.com.gorillaroxo.sanjy.server.core.exception;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class UnhandledClientHttpException extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.UNHANDLED_CLIENT_HTTP;
    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    @Getter
    private final RequestInformation requestInformation;

    public UnhandledClientHttpException(
            final String customMessage, final Throwable originalCause, final RequestInformation requestInformation) {
        super(CODE, STATUS, customMessage, originalCause);
        this.requestInformation = requestInformation;
    }

    public UnhandledClientHttpException(final RequestInformation requestInformation) {
        super(CODE, STATUS);
        this.requestInformation = requestInformation;
    }

    public UnhandledClientHttpException(final Throwable originalCause, final RequestInformation requestInformation) {
        super(CODE, STATUS, originalCause);
        this.requestInformation = requestInformation;
    }

    public UnhandledClientHttpException(final String customMessage, final RequestInformation requestInformation) {
        super(CODE, STATUS, customMessage);
        this.requestInformation = requestInformation;
    }

    @Override
    protected LogLevel getLogLevel() {
        return LogLevel.ERROR;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    public static class RequestInformation implements Serializable {

        @Serial
        private static final long serialVersionUID = 2443045996389729555L;

        private final String feignMethodKey;
        private final String requestMethod;
        private final String requestUrl;
        private final Integer httpStatusCode;

        @Getter
        private final Map<String, Collection<String>> requestHeaders;

        private final String requestBody;

        @Getter
        private final Map<String, Collection<String>> responseHeaders;

        private final String responseBody;

        @Builder
        public RequestInformation(
                final String feignMethodKey,
                final String requestMethod,
                final String requestUrl,
                final Integer httpStatusCode,
                final Map<String, Collection<String>> requestHeaders,
                final String requestBody,
                final Map<String, Collection<String>> responseHeaders,
                final String responseBody) {
            this.feignMethodKey = feignMethodKey;
            this.requestMethod = requestMethod;
            this.requestUrl = requestUrl;
            this.httpStatusCode = httpStatusCode;
            this.requestHeaders = Objects.requireNonNullElseGet(requestHeaders, Collections::emptyMap);
            this.requestBody = requestBody;
            this.responseHeaders = Objects.requireNonNullElseGet(responseHeaders, Collections::emptyMap);
            this.responseBody = responseBody;
        }

        public Optional<String> getFeignMethodKey() {
            return Optional.ofNullable(feignMethodKey).filter(Predicate.not(String::isBlank));
        }

        public Optional<String> getRequestMethod() {
            return Optional.ofNullable(requestMethod).filter(Predicate.not(String::isBlank));
        }

        public Optional<String> getRequestUrl() {
            return Optional.ofNullable(requestUrl).filter(Predicate.not(String::isBlank));
        }

        public Optional<Integer> getHttpStatusCode() {
            return Optional.ofNullable(httpStatusCode);
        }

        public Optional<String> getRequestBody() {
            return Optional.ofNullable(requestBody).filter(Predicate.not(String::isBlank));
        }

        public Optional<String> getResponseBody() {
            return Optional.ofNullable(responseBody).filter(Predicate.not(String::isBlank));
        }
    }
}
