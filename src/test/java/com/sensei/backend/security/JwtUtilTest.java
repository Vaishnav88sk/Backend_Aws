package com.sensei.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Set a dummy secret key for testing (at least 256 bits for HS256)
        ReflectionTestUtils.setField(jwtUtil, "SECRET", "ThisIsAVerySecretKeyForTestingPurposesOnly!");
        // Set expiration time to 1 hour (3600000 ms) so the token doesn't instantly expire
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 3600000L);
    }

    @Test
    void testGenerateTokenAndValidate() {
        String token = jwtUtil.generateToken(testEmail);
        assertNotNull(token);
        
        boolean isValid = jwtUtil.validateToken(token);
        assertTrue(isValid, "Token should be valid");
        
        String extractedEmail = jwtUtil.extractEmail(token);
        assertEquals(testEmail, extractedEmail, "Extracted email should match original email");
    }

    @Test
    void testValidateTokenWithInvalidToken() {
        boolean isValid = jwtUtil.validateToken("invalid.token.here");
        assertFalse(isValid, "Token should be invalid");
    }
}
