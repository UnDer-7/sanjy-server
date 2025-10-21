package br.com.gorillaroxo.sanjy.server.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    UNEXPECTED_ERROR("001", "An unexpected error occurred"),
    INVALID_VALUES("002", "Invalid values"),
    DIET_PLAN_NOT_FOUND("003", "Diet plan was not found");

    private final String code;
    private final String message;
}
