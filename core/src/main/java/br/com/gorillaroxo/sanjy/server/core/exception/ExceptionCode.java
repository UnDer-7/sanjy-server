package br.com.gorillaroxo.sanjy.server.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    UNEXPECTED_ERROR("001", "An unexpected error occurred"),
    INVALID_VALUES("002", "Invalid values"),
    DIET_PLAN_NOT_FOUND("003", "Diet plan was not found"),
    STANDARD_OPTIONS_NOT_IN_SEQUENCE("004", "Standard options is not in sequence"),
    ASSER_FAIL("005", "Some internal validation failed");

    private final String code;
    private final String message;
}
