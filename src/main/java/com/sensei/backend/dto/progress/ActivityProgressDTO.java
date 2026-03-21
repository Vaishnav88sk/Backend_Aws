package com.sensei.backend.dto.progress;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ActivityProgressDTO {

    private UUID activityId;
    private String status;
}