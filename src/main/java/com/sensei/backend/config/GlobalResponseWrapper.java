package com.sensei.backend.config;

import com.sensei.backend.dto.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;

/**
 * Ensures all successful responses are wrapped in ApiResponse<T>
 * @author vaishnav88sk
 */
@RestControllerAdvice(basePackages = "com.sensei.backend.controller")
public class GlobalResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        if (body instanceof ApiResponse || body instanceof ProblemDetail || body instanceof String) {
            return body;
        }

        return ApiResponse.builder()
                .status("SUCCESS")
                .message("Request processed successfully")
                .data(body)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
