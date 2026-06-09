package com.shoppingmall.goods.dto;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    private ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "ok", data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "created", data);
    }
}
