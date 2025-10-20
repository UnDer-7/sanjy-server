package br.com.gorillaroxo.sanjy.server.core.exception;

import lombok.Getter;

import java.util.Optional;
import java.util.function.Predicate;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final String code;
    private final String timestamp;
    private final String errorMessage;
    private final Integer httpStatusCode;
    private final ExceptionCode exceptionCode;
    private final String customMessage;
    private final Throwable originalCause;

    protected BusinessException(
        final ExceptionCode exceptionCode,
        final String customMessage,
        final HttpStatus httpStatus,
        final Throwable originalCause) {

    }

    private static String getExceptionMessage(final ExceptionCode exceptionCode) {
        return "[code: %s] - [msg: %s]".formatted(exceptionCode.getCode(), exceptionCode.getMessage());
    }

    private static String getExceptionMessage(final ExceptionCode exceptionCode, final Throwable throwable) {
        return "[code: %s] - [msg: %s] - [originalCause: %s]".formatted(exceptionCode.getCode(), exceptionCode.getMessage(), throwable.getMessage());
    }

    private static String getExceptionMessage(final ExceptionCode exceptionCode, final String customMessage) {
        return Optional.ofNullable(customMessage)
            .filter(Predicate.not(String::isBlank))
            .map(cm -> "[code: %s] - [msg: %s] - [customMsg: %s]"
                .formatted(exceptionCode.getCode(), exceptionCode.getMessage(), cm))
            .orElseGet(() -> getExceptionMessage(exceptionCode));
    }

    private static String getExceptionMessage(final ExceptionCode exceptionCode, final String customMessage, final Throwable originalCause) {
        return Optional.ofNullable(customMessage)
            .filter(Predicate.not(String::isBlank))
            .map(cm -> "[code: %s] - [msg: %s] - [customMsg: %s] - [originalCause: %s]"
                .formatted(exceptionCode.getCode(), exceptionCode.getMessage(), cm, originalCause.getMessage()))
            .orElseGet(() -> getExceptionMessage(exceptionCode, originalCause));
    }

    protected enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}
