package com.mycomp.api.config.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Daniel
 */
@Getter
@AllArgsConstructor
public class ApiError {

    private final String message;
    private final ApiErrorType type;

    public static enum ApiErrorType {
        AUTHORIZATION, API_ERROR
    }
}
