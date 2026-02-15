package br.com.gorillaroxo.sanjy.server.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class StandardOptionNotFoundException extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.STANDARD_OPTION_NOT_FOUND;
    private static final HttpStatus STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    public StandardOptionNotFoundException(final String customMessage, final Throwable originalCause) {
        super(CODE, STATUS, customMessage, originalCause);
    }

    public StandardOptionNotFoundException() {
        super(CODE, STATUS);
    }

    public StandardOptionNotFoundException(final Throwable originalCause) {
        super(CODE, STATUS, originalCause);
    }

    public StandardOptionNotFoundException(final String customMessage) {
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
