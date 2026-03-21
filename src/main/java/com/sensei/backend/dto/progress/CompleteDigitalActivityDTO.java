package com.sensei.backend.dto.progress;

import lombok.Data;
import java.util.UUID;

@Data
public class CompleteDigitalActivityDTO {
    private UUID childId;
    private UUID digitalActivityId;
}
