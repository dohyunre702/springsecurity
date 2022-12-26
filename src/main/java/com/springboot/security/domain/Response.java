package com.springboot.security.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

//response 추상화 : resultCode를 추가한 Response 선언
@AllArgsConstructor
@Getter
public class Response<T> {
    private String resultCode;
    private T result;

    public static Response<Void> error(String errorCode) {
        return new Response<>(errorCode, null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", result);
    }
}
