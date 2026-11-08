package com.sensei.backend.dto.media;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for requesting a presigned upload URL.
 * <p>
 * Clients supply the original file name and its standard MIME content type.
 * An optional {@code folder} name can be passed to organize assets in the R2 bucket.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUploadRequestDTO {

    /**
     * Original name of the file to be uploaded (e.g., "diagram.png", "walkthrough.mp4").
     */
    @NotBlank(message = "File name is required")
    private String fileName;

    /**
     * Standard MIME type of the file (e.g., "image/png", "image/gif", "video/mp4").
     * Must be one of the supported media types.
     */
    @NotBlank(message = "Content type is required")
    private String contentType;

    /**
     * Target directory prefix to organize assets within the bucket
     * (e.g., "interactive-process", "avatars", "activities").
     * Defaults to "media" when omitted.
     */
    private String folder;
}
