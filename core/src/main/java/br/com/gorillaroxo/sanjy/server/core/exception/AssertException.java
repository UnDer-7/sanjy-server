package br.com.gorillaroxo.sanjy.server.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class AssertException extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.ASSER_FAIL;
    private static final HttpStatus STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    public AssertException(final String customMessage, final Throwable originalCause) {
        super(CODE, STATUS, customMessage, originalCause);
    }

    public AssertException() {
        super(CODE, STATUS);
    }

    public AssertException(final Throwable originalCause) {
        super(CODE, STATUS, originalCause);
    }

    public AssertException(final String customMessage) {
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
