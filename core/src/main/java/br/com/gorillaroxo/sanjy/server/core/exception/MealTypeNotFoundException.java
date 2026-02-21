package br.com.gorillaroxo.sanjy.server.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class MealTypeNotFoundException extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.MEAL_TYPE_NOT_FOUND;
    private static final HttpStatus STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    public MealTypeNotFoundException(final String customMessage, final Throwable originalCause) {
        super(CODE, STATUS, customMessage, originalCause);
    }

    public MealTypeNotFoundException() {
        super(CODE, STATUS);
    }

    public MealTypeNotFoundException(final Throwable originalCause) {
        super(CODE, STATUS, originalCause);
    }

    public MealTypeNotFoundException(final String customMessage) {
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
