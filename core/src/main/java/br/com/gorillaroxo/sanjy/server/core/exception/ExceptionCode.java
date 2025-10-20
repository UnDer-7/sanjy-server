package br.com.gorillaroxo.sanjy.server.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    UNEXPECTED_ERROR("001", "An unexpected error occurred"),
    INVALID_VALUES("002", "Invalid values");

    public final String code;
    public final String message;
}
