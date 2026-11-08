package com.sensei.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Configuration class for Cloudflare R2 object storage integration.
 * <p>
 * Cloudflare R2 is an S3-compatible zero-egress object storage service.
 * This class establishes connectivity using the AWS SDK v2, configuring:
 * <ul>
 *   <li>Custom S3 endpoint pointing to Cloudflare's regional gateway.</li>
 *   <li>Static credentials provider with R2 API token access and secret keys.</li>
 *   <li>Path-style access enabled to match Cloudflare R2 URL format without SSL wildcard certificate issues.</li>
 * </ul>
 */
@Configuration
public class CloudflareR2Config {

    @Value("${cloudflare.r2.account-id:}")
    private String accountId;

    @Value("${cloudflare.r2.access-key:}")
    private String accessKey;

    @Value("${cloudflare.r2.secret-key:}")
    private String secretKey;

    /**
     * Constructs the Cloudflare R2 base endpoint URI using the account ID.
     * Falls back to a dummy identifier when unconfigured to allow test contexts to load.
     *
     * @return URI representing the Cloudflare R2 API endpoint
     */
    private URI getR2Endpoint() {
        String id = StringUtils.hasText(accountId) ? accountId.trim() : "dummy-account";
        return URI.create(String.format("https://%s.r2.cloudflarestorage.com", id));
    }

    /**
     * Creates the static AWS credentials provider using Cloudflare R2 token credentials.
     * Falls back to dummy credentials when unconfigured to prevent context initialization failure.
     *
     * @return {@link StaticCredentialsProvider} configured with access and secret keys
     */
    private StaticCredentialsProvider getCredentialsProvider() {
        String key = StringUtils.hasText(accessKey) ? accessKey.trim() : "dummy-access-key";
        String secret = StringUtils.hasText(secretKey) ? secretKey.trim() : "dummy-secret-key";
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(key, secret));
    }

    /**
     * Builds standard S3 client configuration required for Cloudflare R2.
     * <p>
     * {@code pathStyleAccessEnabled(true)} enforces URLs in the format
     * {@code https://<account_id>.r2.cloudflarestorage.com/<bucket>/<key>}
     * instead of virtual-host styled subdomains.
     *
     * @return configured {@link S3Configuration}
     */
    private S3Configuration getS3Configuration() {
        return S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();
    }

    /**
     * Configures the {@link S3Presigner} bean used for generating secure, temporary
     * presigned upload URLs for clients (frontend) to perform direct uploads.
     *
     * @return configured {@link S3Presigner} instance
     */
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .endpointOverride(getR2Endpoint())
                .region(Region.of("auto"))
                .credentialsProvider(getCredentialsProvider())
                .serviceConfiguration(getS3Configuration())
                .build();
    }

    /**
     * Configures the {@link S3Client} bean used for direct server-side S3 operations
     * such as object deletion, metadata checks, and bucket validations.
     *
     * @return configured {@link S3Client} instance
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(getR2Endpoint())
                .region(Region.of("auto"))
                .credentialsProvider(getCredentialsProvider())
                .serviceConfiguration(getS3Configuration())
                .build();
    }
}
