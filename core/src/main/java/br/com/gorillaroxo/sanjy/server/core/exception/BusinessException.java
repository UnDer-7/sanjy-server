package br.com.gorillaroxo.sanjy.server.core.exception;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final String timestamp;
    private final int httpStatusCode;
    private final Throwable originalCause;
    private final ExceptionCode exceptionCode;

    @Getter(AccessLevel.NONE)
    private final String customMessage;

    protected BusinessException(
            final ExceptionCode exceptionCode,
            final HttpStatus httpStatus,
            final String customMessage,
            final Throwable originalCause) {

        super(getExceptionMessage(exceptionCode, customMessage, originalCause), originalCause);

        this.exceptionCode = exceptionCode;
        this.timestamp =
                LocalDateTime.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        this.customMessage = customMessage;
        this.httpStatusCode = httpStatus.getValue();
        this.originalCause = originalCause;
    }

    protected BusinessException(final ExceptionCode exceptionCode, final HttpStatus httpStatus) {

        super(getExceptionMessage(exceptionCode));

        this.exceptionCode = exceptionCode;
        this.timestamp =
                LocalDateTime.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        this.customMessage = null;
        this.httpStatusCode = httpStatus.getValue();
        this.originalCause = null;
    }

    protected BusinessException(
            final ExceptionCode exceptionCode, final HttpStatus httpStatus, final Throwable originalCause) {

        super(getExceptionMessage(exceptionCode, originalCause), originalCause);

        this.exceptionCode = exceptionCode;
        this.timestamp =
                LocalDateTime.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        this.customMessage = null;
        this.httpStatusCode = httpStatus.getValue();
        this.originalCause = originalCause;
    }

    protected BusinessException(
            final ExceptionCode exceptionCode, final HttpStatus httpStatus, final String customMessage) {

        super(getExceptionMessage(exceptionCode, customMessage));

        this.exceptionCode = exceptionCode;
        this.timestamp =
                LocalDateTime.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        this.customMessage = customMessage;
        this.httpStatusCode = httpStatus.getValue();
        this.originalCause = null;
    }

    public void executeLogging() {
        final var className = this.getClass().getSimpleName();
        final var defaultMsg = "An exception has occurred";

        switch (getLogLevel()) {
            case TRACE ->
                getLogger()
                        .trace(
                                LogField.Placeholders.THREE.placeholder,
                                StructuredArguments.kv(LogField.MSG.label(), defaultMsg),
                                StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), className),
                                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), super.getMessage()));
            case DEBUG ->
                getLogger()
                        .debug(
                                LogField.Placeholders.THREE.placeholder,
                                StructuredArguments.kv(LogField.MSG.label(), defaultMsg),
                                StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), className),
                                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), super.getMessage()));
            case INFO ->
                getLogger()
                        .info(
                                LogField.Placeholders.THREE.placeholder,
                                StructuredArguments.kv(LogField.MSG.label(), defaultMsg),
                                StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), className),
                                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), super.getMessage()));
            case WARN ->
                getLogger()
                        .warn(
                                LogField.Placeholders.THREE.placeholder,
                                StructuredArguments.kv(LogField.MSG.label(), defaultMsg),
                                StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), className),
                                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), super.getMessage()));
            case ERROR ->
                getLogger()
                        .error(
                                LogField.Placeholders.THREE.placeholder,
                                StructuredArguments.kv(LogField.MSG.label(), defaultMsg),
                                StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), className),
                                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), super.getMessage()));
        }
    }

    public Optional<String> getCustomMessage() {
        return Optional.ofNullable(customMessage);
    }

    protected abstract LogLevel getLogLevel();

    protected abstract Logger getLogger();

    private static String getExceptionMessage(final ExceptionCode exceptionCode) {
        return "[code: %s] - [msg: %s]".formatted(exceptionCode.getCode(), exceptionCode.getMessage());
    }

    private static String getExceptionMessage(final ExceptionCode exceptionCode, final Throwable throwable) {
        return "[code: %s] - [msg: %s] - [originalCause: %s]"
                .formatted(exceptionCode.getCode(), exceptionCode.getMessage(), throwable.getMessage());
    }

    private static String getExceptionMessage(final ExceptionCode exceptionCode, final String customMessage) {
        return Optional.ofNullable(customMessage)
                .filter(Predicate.not(String::isBlank))
                .map(cm -> "[code: %s] - [msg: %s] - [customMsg: %s]"
                        .formatted(exceptionCode.getCode(), exceptionCode.getMessage(), cm))
                .orElseGet(() -> getExceptionMessage(exceptionCode));
    }

    private static String getExceptionMessage(
            final ExceptionCode exceptionCode, final String customMessage, final Throwable originalCause) {
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
