package com.sensei.backend.dto.progress;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChildProgressSummaryDTO {

    private long totalSubModules;
    private long completedSubModules;
    private double progressPercentage;

    private long totalActivities;
    private long completedActivities;

    private long totalDigitals;
    private long completedDigitals;
}