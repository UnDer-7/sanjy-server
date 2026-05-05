package br.com.gorillaroxo.sanjy.server.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class MealTypeDuplicateNameException extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.MEAL_TYPE_DUPLICATE_NAME;
    private static final HttpStatus STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    public MealTypeDuplicateNameException(final String customMessage) {
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
