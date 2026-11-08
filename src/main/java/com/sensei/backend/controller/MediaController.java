package com.sensei.backend.controller;

import com.sensei.backend.dto.media.PresignedUploadRequestDTO;
import com.sensei.backend.dto.media.PresignedUploadResponseDTO;
import com.sensei.backend.service.MediaStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for media asset lifecycle operations and presigned direct-upload authorizations.
 * <p>
 * Provides endpoints for frontend applications to obtain secure Cloudflare R2 presigned PUT URLs
 * and clean up deleted media assets.
 */
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaStorageService mediaStorageService;

    /**
     * Generates a secure, temporary presigned PUT URL allowing clients to upload media
     * (pictures, GIFs, and videos) directly to Cloudflare R2.
     *
     * @param request metadata of the asset to upload, including file name, MIME type, and folder
     * @return 200 OK with {@link PresignedUploadResponseDTO} containing upload URL and CDN public URL
     */
    @PostMapping("/presigned-upload-url")
    public ResponseEntity<PresignedUploadResponseDTO> getPresignedUploadUrl(
            @Valid @RequestBody PresignedUploadRequestDTO request) {
        return ResponseEntity.ok(mediaStorageService.generatePresignedUploadUrl(request));
    }

    /**
     * Deletes a media file from Cloudflare R2 given its bucket key or full public CDN URL.
     *
     * @param fileKeyOrUrl relative key (e.g. {@code media/2026/11/uuid.png}) or full CDN URL
     * @return 204 No Content
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteMedia(@RequestParam String fileKeyOrUrl) {
        mediaStorageService.deleteFile(fileKeyOrUrl);
        return ResponseEntity.noContent().build();
    }
}
