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
    EXCEPTION_CAUSE_MSG;

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
