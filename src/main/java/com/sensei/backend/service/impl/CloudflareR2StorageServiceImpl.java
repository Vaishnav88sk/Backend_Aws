package com.sensei.backend.service.impl;

import com.sensei.backend.dto.media.PresignedUploadRequestDTO;
import com.sensei.backend.dto.media.PresignedUploadResponseDTO;
import com.sensei.backend.exception.ValidationException;
import com.sensei.backend.service.MediaStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Production-ready implementation of {@link MediaStorageService} backed by Cloudflare R2.
 * <p>
 * Key capabilities:
 * <ul>
 *   <li>Strict MIME type validation restricting uploads to images, GIFs, and videos.</li>
 *   <li>Dynamic key partitioning by folder, year, and month to prevent prefix throttling.</li>
 *   <li>UUID-based object naming to prevent file collisions and path traversal attacks.</li>
 *   <li>Integration with Cloudflare Custom Domain for zero-egress, edge-cached media delivery.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudflareR2StorageServiceImpl implements MediaStorageService {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket-name:}")
    private String bucketName;

    @Value("${cloudflare.r2.public-domain:}")
    private String publicDomain;

    @Value("${cloudflare.r2.presigned-url-expiration-minutes:10}")
    private long expirationMinutes;

    /**
     * Whitelist mapping of allowed MIME types to standard file extensions.
     */
    private static final Map<String, String> ALLOWED_MIME_TYPES = Map.ofEntries(
            Map.entry("image/jpeg", ".jpg"),
            Map.entry("image/png", ".png"),
            Map.entry("image/webp", ".webp"),
            Map.entry("image/gif", ".gif"),
            Map.entry("image/svg+xml", ".svg"),
            Map.entry("video/mp4", ".mp4"),
            Map.entry("video/webm", ".webm"),
            Map.entry("video/quicktime", ".mov")
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public PresignedUploadResponseDTO generatePresignedUploadUrl(PresignedUploadRequestDTO request) {
        String contentType = request.getContentType().toLowerCase().trim();
        if (!ALLOWED_MIME_TYPES.containsKey(contentType)) {
            throw new ValidationException("Unsupported content type: " + contentType + 
                    ". Allowed types: images (jpeg, png, webp, gif, svg) and videos (mp4, webm, mov).");
        }

        String extension = ALLOWED_MIME_TYPES.get(contentType);
        String safeFolder = sanitizeFolder(request.getFolder());

        // Partition by year and month to avoid S3 prefix saturation: {folder}/2026/11/uuid.ext
        LocalDate now = LocalDate.now();
        String fileKey = String.format("%s/%d/%02d/%s%s", 
                safeFolder, now.getYear(), now.getMonthValue(), UUID.randomUUID(), extension);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);

        String uploadUrl = presigned.url().toString();
        String publicUrl = buildPublicUrl(fileKey);

        log.info("Generated presigned upload URL for key: {}", fileKey);

        return PresignedUploadResponseDTO.builder()
                .uploadUrl(uploadUrl)
                .publicUrl(publicUrl)
                .fileKey(fileKey)
                .expiresInSeconds(expirationMinutes * 60)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFile(String fileKeyOrUrl) {
        if (!StringUtils.hasText(fileKeyOrUrl)) {
            return;
        }

        String fileKey = extractFileKey(fileKeyOrUrl);
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("Successfully deleted media file from R2: {}", fileKey);
        } catch (Exception e) {
            log.error("Failed to delete media file from R2: {}", fileKey, e);
        }
    }

    /**
     * Cleans and normalizes folder names, protecting against directory traversal.
     *
     * @param folder user-supplied folder or category name
     * @return sanitized folder name or fallback "media"
     */
    private String sanitizeFolder(String folder) {
        if (!StringUtils.hasText(folder)) {
            return "media";
        }
        // Strip out non-alphanumeric, dots, slashes to prevent traversal attacks
        String cleaned = folder.replaceAll("[^a-zA-Z0-9-_]", "").toLowerCase();
        return cleaned.isEmpty() ? "media" : cleaned;
    }

    /**
     * Formats the permanent CDN URL for client retrieval.
     *
     * @param fileKey the relative key inside the bucket
     * @return fully-qualified public CDN URL
     */
    private String buildPublicUrl(String fileKey) {
        if (StringUtils.hasText(publicDomain)) {
            String domain = publicDomain.endsWith("/") ? publicDomain.substring(0, publicDomain.length() - 1) : publicDomain;
            return domain + "/" + fileKey;
        }
        return fileKey;
    }

    /**
     * Extracts the relative S3 key from a full public CDN URL if needed.
     *
     * @param fileKeyOrUrl relative key or full CDN URL
     * @return the relative object key
     */
    private String extractFileKey(String fileKeyOrUrl) {
        if (StringUtils.hasText(publicDomain) && fileKeyOrUrl.startsWith(publicDomain)) {
            String sub = fileKeyOrUrl.substring(publicDomain.length());
            return sub.startsWith("/") ? sub.substring(1) : sub;
        }
        return fileKeyOrUrl;
    }
}
