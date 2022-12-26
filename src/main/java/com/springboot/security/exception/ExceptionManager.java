package com.springboot.security.exception;

//RuntimeException과 HospitalReviewException에 대한 예외처리 함수 작성
//응답객체에 상태와 error메시지 반환

import com.springboot.security.domain.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(e.getMessage()));
    }

    @ExceptionHandler(HospitalReviewException.class)
    public ResponseEntity<?> hospitalReviewExceptionHandler(HospitalReviewException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }
}