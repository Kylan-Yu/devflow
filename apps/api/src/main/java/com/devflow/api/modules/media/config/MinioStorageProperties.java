package com.devflow.api.modules.media.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "devflow.storage.minio")
public class MinioStorageProperties {

    private boolean enabled;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String publicBaseUrl;
    private String avatarPrefix = "avatars";
    private String postCoverPrefix = "post-covers";
    private long maxFileSizeBytes = 5L * 1024 * 1024;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public String getAvatarPrefix() {
        return avatarPrefix;
    }

    public void setAvatarPrefix(String avatarPrefix) {
        this.avatarPrefix = avatarPrefix;
    }

    public String getPostCoverPrefix() {
        return postCoverPrefix;
    }

    public void setPostCoverPrefix(String postCoverPrefix) {
        this.postCoverPrefix = postCoverPrefix;
    }

    public long getMaxFileSizeBytes() {
        return maxFileSizeBytes;
    }

    public void setMaxFileSizeBytes(long maxFileSizeBytes) {
        this.maxFileSizeBytes = maxFileSizeBytes;
    }
}
