package com.sensei.backend.dto.progress;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SubModuleProgressDTO {

    private UUID subModuleId;
    private boolean completed;
    private Double score;
}
