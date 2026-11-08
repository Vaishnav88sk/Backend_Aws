package com.sensei.backend.service;

import com.sensei.backend.dto.media.PresignedUploadRequestDTO;
import com.sensei.backend.dto.media.PresignedUploadResponseDTO;
import com.sensei.backend.exception.ValidationException;
import com.sensei.backend.service.impl.CloudflareR2StorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudflareR2StorageServiceImplTest {

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private CloudflareR2StorageServiceImpl mediaStorageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mediaStorageService, "bucketName", "sensei-media");
        ReflectionTestUtils.setField(mediaStorageService, "publicDomain", "https://media.mysite.com");
        ReflectionTestUtils.setField(mediaStorageService, "expirationMinutes", 10L);
    }

    @Test
    void generatePresignedUploadUrl_success_forImage() throws Exception {
        PresignedPutObjectRequest presignedPutObjectRequest = mock(PresignedPutObjectRequest.class);
        when(presignedPutObjectRequest.url()).thenReturn(URI.create("https://r2.cloudflarestorage.com/signed-url").toURL());
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(presignedPutObjectRequest);

        PresignedUploadRequestDTO request = PresignedUploadRequestDTO.builder()
                .fileName("picture.png")
                .contentType("image/png")
                .folder("avatars")
                .build();

        PresignedUploadResponseDTO response = mediaStorageService.generatePresignedUploadUrl(request);

        assertNotNull(response);
        assertEquals("https://r2.cloudflarestorage.com/signed-url", response.getUploadUrl());
        assertTrue(response.getPublicUrl().startsWith("https://media.mysite.com/avatars/"));
        assertTrue(response.getFileKey().endsWith(".png"));
        assertEquals(600, response.getExpiresInSeconds());
    }

    @Test
    void generatePresignedUploadUrl_success_forVideo() throws Exception {
        PresignedPutObjectRequest presignedPutObjectRequest = mock(PresignedPutObjectRequest.class);
        when(presignedPutObjectRequest.url()).thenReturn(URI.create("https://r2.cloudflarestorage.com/signed-url").toURL());
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(presignedPutObjectRequest);

        PresignedUploadRequestDTO request = PresignedUploadRequestDTO.builder()
                .fileName("lesson.mp4")
                .contentType("video/mp4")
                .build();

        PresignedUploadResponseDTO response = mediaStorageService.generatePresignedUploadUrl(request);

        assertNotNull(response);
        assertTrue(response.getPublicUrl().startsWith("https://media.mysite.com/media/"));
        assertTrue(response.getFileKey().endsWith(".mp4"));
    }

    @Test
    void generatePresignedUploadUrl_invalidContentType_throwsValidationException() {
        PresignedUploadRequestDTO request = PresignedUploadRequestDTO.builder()
                .fileName("script.sh")
                .contentType("application/x-sh")
                .build();

        assertThrows(ValidationException.class, () -> mediaStorageService.generatePresignedUploadUrl(request));
    }

    @Test
    void deleteFile_callsS3Client() {
        mediaStorageService.deleteFile("https://media.mysite.com/media/2026/11/sample.png");

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFile_withPresetAvatar_doesNotCallS3Client() {
        mediaStorageService.deleteFile("Fox");

        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void toStorageKey_normalizesFullUrlAndPreservesKeyAndPreset() {
        assertEquals("subjects/2026/11/math.png", 
                mediaStorageService.toStorageKey("https://media.mysite.com/subjects/2026/11/math.png"));
        assertEquals("avatars/2026/11/child.jpg", 
                mediaStorageService.toStorageKey("avatars/2026/11/child.jpg"));
        assertEquals("Fox", 
                mediaStorageService.toStorageKey("Fox"));
        assertNull(mediaStorageService.toStorageKey(null));
    }

    @Test
    void toPublicUrl_prependsDomainForKeysAndPreservesFullUrlsAndPresets() {
        assertEquals("https://media.mysite.com/subjects/2026/11/math.png",
                mediaStorageService.toPublicUrl("subjects/2026/11/math.png"));
        assertEquals("https://media.mysite.com/avatars/child.jpg",
                mediaStorageService.toPublicUrl("https://media.mysite.com/avatars/child.jpg"));
        assertEquals("Blue Bird",
                mediaStorageService.toPublicUrl("Blue Bird"));
        assertNull(mediaStorageService.toPublicUrl(null));
    }
}
