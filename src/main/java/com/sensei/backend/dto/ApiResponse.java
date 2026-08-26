package com.sensei.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiResponse<T> {

    private String status; // SUCCESS / ERROR
    private String message;
    private T data;
    private LocalDateTime timestamp;
}
