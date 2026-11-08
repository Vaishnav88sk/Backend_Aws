package com.sensei.backend.dto.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) returned upon successful presigned URL generation.
 * <p>
 * Contains the temporary presigned upload endpoint for client-side direct HTTP PUT,
 * alongside the permanent public CDN delivery URL and bucket file key.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUploadResponseDTO {

    /**
     * Temporary pre-signed URL for direct HTTP PUT upload by the client (frontend).
     * Valid only for the duration specified in {@link #expiresInSeconds}.
     */
    private String uploadUrl;

    /**
     * Permanent public URL served via Cloudflare CDN / Custom Domain
     * (e.g. {@code https://media.yourdomain.com/media/2026/11/uuid.png}).
     * This URL should be persisted in entities (e.g., {@code media_url}).
     */
    private String publicUrl;

    /**
     * Relative object key inside the Cloudflare R2 bucket
     * (e.g. {@code media/2026/11/550e8400-e29b-41d4-a716-446655440000.mp4}).
     */
    private String fileKey;

    /**
     * Time-to-live (TTL) of the {@link #uploadUrl} in seconds.
     */
    private long expiresInSeconds;
}
