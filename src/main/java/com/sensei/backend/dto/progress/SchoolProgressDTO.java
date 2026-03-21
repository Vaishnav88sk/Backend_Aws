package com.sensei.backend.dto.progress;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchoolProgressDTO {

    private String schoolName;

    private long totalChildren;
    private long activeChildren;

    private double averageProgress;

    private long completedSubModules;
}