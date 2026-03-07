package br.com.gorillaroxo.sanjy.server.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class RepeatedMealTypeNamesException extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.REPEATED_MEAL_TYPE_NAMES;
    private static final HttpStatus STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    public RepeatedMealTypeNamesException(final String customMessage, final Throwable originalCause) {
        super(CODE, STATUS, customMessage, originalCause);
    }

    public RepeatedMealTypeNamesException() {
        super(CODE, STATUS);
    }

    public RepeatedMealTypeNamesException(final Throwable originalCause) {
        super(CODE, STATUS, originalCause);
    }

    public RepeatedMealTypeNamesException(final String customMessage) {
        super(CODE, STATUS, customMessage);
    }

    @Override
    protected LogLevel getLogLevel() {
        return LogLevel.WARN;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
