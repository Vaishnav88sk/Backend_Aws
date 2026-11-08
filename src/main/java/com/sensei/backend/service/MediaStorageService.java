package com.sensei.backend.service;

import com.sensei.backend.dto.media.PresignedUploadRequestDTO;
import com.sensei.backend.dto.media.PresignedUploadResponseDTO;

/**
 * Service contract defining operations for media asset management,
 * presigned direct-upload authorizations, and asset lifecycle management
 * backed by object storage (e.g. Cloudflare R2).
 */
public interface MediaStorageService {

    /**
     * Generates a temporary presigned upload URL enabling client-side direct HTTP PUT upload.
     * <p>
     * Validates MIME content types against supported media formats (images, GIFs, videos),
     * sanitizes path inputs, generates non-colliding UUID file keys partitioned by year and month,
     * and signs the request using S3 credentials.
     *
     * @param request encapsulates the original file name, MIME type, and optional folder categorization
     * @return {@link PresignedUploadResponseDTO} containing upload endpoint, permanent CDN URL, and expiration
     * @throws com.sensei.backend.exception.ValidationException if the supplied MIME type is unsupported
     */
    PresignedUploadResponseDTO generatePresignedUploadUrl(PresignedUploadRequestDTO request);

    /**
     * Permanently removes an asset from object storage by its relative object key
     * or its full public CDN URL.
     *
     * @param fileKeyOrUrl relative bucket key (e.g., {@code media/2026/11/uuid.png}) or
     *                     full CDN URL (e.g., {@code https://media.example.com/media/2026/11/uuid.png})
     */
    void deleteFile(String fileKeyOrUrl);

    /**
     * Converts a full public CDN URL (or relative key) into a normalized relative storage key
     * for persistence in database entities.
     * <p>
     * Examples:
     * <ul>
     *   <li>{@code "https://media.domain.com/subjects/2026/11/abc.png"} &rarr; {@code "subjects/2026/11/abc.png"}</li>
     *   <li>{@code "subjects/2026/11/abc.png"} &rarr; {@code "subjects/2026/11/abc.png"}</li>
     *   <li>{@code "Fox"} (preset avatar) &rarr; {@code "Fox"}</li>
     * </ul>
     *
     * @param fileKeyOrUrl the input URL, key, or preset identifier
     * @return normalized relative storage key or preset identifier
     */
    String toStorageKey(String fileKeyOrUrl);

    /**
     * Converts a relative storage key into a fully-qualified public CDN URL for API responses.
     * If the input is already a full URL or a preset avatar (e.g., "Fox"), it is returned as-is.
     *
     * @param fileKeyOrUrl relative storage key, full URL, or preset identifier
     * @return fully-qualified public CDN URL or preset identifier
     */
    String toPublicUrl(String fileKeyOrUrl);
}
