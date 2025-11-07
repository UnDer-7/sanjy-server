package br.com.gorillaroxo.sanjy.server.core.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogField {
    MSG,
    EXCEPTION_CLASS,
    EXCEPTION_MESSAGE,
    ERROR_CODE,
    ERROR_TIMESTAMP,
    ERROR_MESSAGE,
    CUSTOM_ERROR_MESSAGE,
    HTTP_STATUS_CODE,
    CUSTOM_EXCEPTION_STACK_TRACE,
    EXCEPTION_CAUSE,
    EXCEPTION_CAUSE_MSG,
    STANDARD_OPTIONS_OPTION_NUMBER,
    MEAL_TYPE_NAME,
    MEAL_TYPE_ID,
    STANDARD_OPTIONS_LIST,
    STANDARD_OPTIONS_SIZE,
    REQUEST_BODY,
    DIET_PLAN_NAME,
    DIET_PLAN_MEAL_TYPE_SIZE,
    DIET_PLAN_ID,
    DIET_PLAN_CREATED_AT,
    DIET_PLAN_IS_ACTIVE,
    RESPONSE_BODY,
    MEAL_RECORD_MEAL_TYPE_ID,
    MEAL_RECORD_IS_FREE_MEAL,
    MEAL_RECORD_STANDARD_OPTION_ID,
    MEAL_RECORD_FREE_MEAL_DESCRIPTION,
    MEAL_RECORD_CONSUMED_AT,
    MEAL_RECORD_ID,
    MEAL_RECORD_CREATED_AT,
    CONSUMED_AT_AFTER,
    CONSUMED_AT_BEFORE,
    MEAL_RECORD_SIZE,
    MEAL_RECORD_SEARCH_PAGE_NUMBER,
    MEAL_RECORD_SEARCH_PAGE_SIZE,
    MEAL_RECORD_SEARCH_CONSUMED_AT_AFTER,
    MEAL_RECORD_SEARCH_CONSUMED_AT_BEFORE,
    MEAL_RECORD_SEARCH_IS_FREE_MEAL,
    MEAL_RECORD_SEARCH_TOTAL_PAGES,
    MEAL_RECORD_SEARCH_CURRENT_PAGE,
    MEAL_RECORD_SEARCH_TOTAL_ITEMS,
    MEAL_RECORD_SEARCH_CONTENT_SIZE,
    TRANSACTION_ID,
    HTTP_REQUEST,
    CORRELATION_ID,
    CHANNEL,
    FREE_MEAL_QUANTITY,
    PLANNED_MEAL_QUANTITY,
    MEAL_QUANTITY;

    public String label() {
        return this.name().toLowerCase();
    }

    @RequiredArgsConstructor
    public enum Placeholders {
        ONE(createPlaceholder(1)),
        TWO(createPlaceholder(2)),
        THREE(createPlaceholder(3)),
        FOUR(createPlaceholder(4)),
        FIVE(createPlaceholder(5)),
        SIX(createPlaceholder(6)),
        SEVEN(createPlaceholder(7)),
        EIGHT(createPlaceholder(8)),
        NINE(createPlaceholder(9)),
        TEN(createPlaceholder(10));

        public final String placeholder;

        public static String createPlaceholder(final int total) {
            return "{} ".repeat(total);
        }
    }
}
