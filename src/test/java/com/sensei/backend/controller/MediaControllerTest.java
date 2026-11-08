package com.sensei.backend.controller;

import com.sensei.backend.dto.media.PresignedUploadRequestDTO;
import com.sensei.backend.dto.media.PresignedUploadResponseDTO;
import com.sensei.backend.service.MediaStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private MediaStorageService mediaStorageService;

    @InjectMocks
    private MediaController mediaController;

    @Test
    void getPresignedUploadUrl_returnsSuccessResponse() {
        PresignedUploadRequestDTO request = PresignedUploadRequestDTO.builder()
                .fileName("test.png")
                .contentType("image/png")
                .folder("media")
                .build();

        PresignedUploadResponseDTO expectedResponse = PresignedUploadResponseDTO.builder()
                .uploadUrl("https://r2.cloudflarestorage.com/signed-url")
                .publicUrl("https://media.mysite.com/media/test.png")
                .fileKey("media/test.png")
                .expiresInSeconds(600)
                .build();

        when(mediaStorageService.generatePresignedUploadUrl(request)).thenReturn(expectedResponse);

        ResponseEntity<PresignedUploadResponseDTO> response = mediaController.getPresignedUploadUrl(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("https://r2.cloudflarestorage.com/signed-url", response.getBody().getUploadUrl());
    }

    @Test
    void deleteMedia_returnsNoContent() {
        doNothing().when(mediaStorageService).deleteFile("media/test.png");

        ResponseEntity<Void> response = mediaController.deleteMedia("media/test.png");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(mediaStorageService, times(1)).deleteFile("media/test.png");
    }
}
