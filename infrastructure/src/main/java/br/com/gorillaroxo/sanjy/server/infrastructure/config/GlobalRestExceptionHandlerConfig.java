package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.exception.BusinessException;
import br.com.gorillaroxo.sanjy.server.core.exception.InvalidValuesException;
import br.com.gorillaroxo.sanjy.server.core.exception.UnexpectedErrorException;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDTO;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.BusinessExceptionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalRestExceptionHandlerConfig extends ResponseEntityExceptionHandler {

    private final BusinessExceptionMapper businessExceptionMapper;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(final BusinessException exception) {
        return logExceptionAndBuild(exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(final Exception exception) {
        if (exception.getCause() instanceof BusinessException businessException) {
            log.warn(
                LogField.Placeholders.FIVE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "An unexpected exception occurred but with a BusinessException cause, delegating to BusinessException handler"),
                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), exception.getMessage()),
                StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), exception.getClass().getSimpleName()),
                StructuredArguments.kv(LogField.EXCEPTION_CAUSE.label(), exception.getCause()),
                StructuredArguments.kv(LogField.EXCEPTION_CAUSE_MSG.label(), exception.getCause().getMessage()),
                exception);

            return handleBusinessException(businessException);
        }

        log.warn(
            "{}",
            StructuredArguments.kv(LogField.MSG.label(), "An unexpected exception occurred"),
            StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), exception.getMessage()),
            exception);

        final var unexpectedException = new UnexpectedErrorException(exception);
        return logExceptionAndBuild(unexpectedException);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleUnexpectedException(final ConstraintViolationException exception) {
        final String invalidValues = exception.getConstraintViolations()
            .stream()
            .map(violation -> buildInvalidAttributeMessage(
                violation.getPropertyPath().toString().split("\\.")[1],
                violation.getMessage(),
                violation.getInvalidValue()))
            .collect(Collectors.joining(" | "));

        return logExceptionAndBuild(new InvalidValuesException(invalidValues));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
        final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {

        final String invalidValues = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> buildInvalidAttributeMessage(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()))
            .collect(Collectors.joining(" | "));

        return logExceptionAndBuild(new InvalidValuesException(invalidValues));
    }

    private static String buildInvalidAttributeMessage(final String attributeName, final String errMotive, final Object attributeValue) {
        return "[ propertyPath: %s - errorMotive: %s - valueProvided: %s ]".formatted(attributeName, errMotive, attributeValue);
    }

    private ResponseEntity<Object> logExceptionAndBuild(final BusinessException exception) {
        try {
            MDC.put(LogField.ERROR_CODE.label(), exception.getExceptionCode().getCode());
            MDC.put(LogField.ERROR_TIMESTAMP.label(), exception.getTimestamp());
            MDC.put(LogField.ERROR_MESSAGE.label(), exception.getExceptionCode().getMessage());
            MDC.put(LogField.HTTP_STATUS_CODE.label(), Integer.toString(exception.getHttpStatusCode()));
            MDC.put(LogField.CUSTOM_EXCEPTION_STACK_TRACE.label(), Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("; ")));
            exception.getCustomMessage().ifPresent(customMsg -> MDC.put(LogField.CUSTOM_ERROR_MESSAGE.label(), customMsg));

            exception.executeLogging();

            final ErrorResponseDTO errorDTO = businessExceptionMapper.toDTO(exception);
            return ResponseEntity.status(exception.getHttpStatusCode()).body(errorDTO);
        } finally {
            MDC.clear();
        }
    }

}
