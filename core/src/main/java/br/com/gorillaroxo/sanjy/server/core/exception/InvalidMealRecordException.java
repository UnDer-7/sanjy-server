package br.com.gorillaroxo.sanjy.server.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class InvalidMealRecordException extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.INVALID_MEAL_RECORD;
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public InvalidMealRecordException(final String customMessage, final Throwable originalCause) {
        super(CODE, STATUS, customMessage, originalCause);
    }

    public InvalidMealRecordException() {
        super(CODE, STATUS);
    }

    public InvalidMealRecordException(final Throwable originalCause) {
        super(CODE, STATUS, originalCause);
    }

    public InvalidMealRecordException(final String customMessage) {
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
