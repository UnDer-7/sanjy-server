package br.com.gorillaroxo.sanjy.server.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class StandardOptionsNotInSequence extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.STANDARD_OPTIONS_NOT_IN_SEQUENCE;
    private static final HttpStatus STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    public StandardOptionsNotInSequence(final String customMessage, final Throwable originalCause) {
        super(CODE, STATUS, customMessage, originalCause);
    }

    public StandardOptionsNotInSequence() {
        super(CODE, STATUS);
    }

    public StandardOptionsNotInSequence(final Throwable originalCause) {
        super(CODE, STATUS, originalCause);
    }

    public StandardOptionsNotInSequence(final String customMessage) {
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
