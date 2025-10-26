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
    INVALID_MEAL_RECORD("005", "Meal record has invalid values"),
    REPEATED_MEAL_TYPE_NAMES("006", "Meal type names has repeated values");

    private final String code;
    private final String message;
}
