package com.sensei.backend.dto.progress;

import lombok.Data;
import java.util.UUID;

@Data
public class StartDigitalActivityDTO {
    private UUID childId;
    private UUID digitalActivityId;
}
