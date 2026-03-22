package com.devflow.api.modules.media.service;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.exception.BusinessException;
import com.devflow.api.modules.media.config.MinioStorageProperties;
import com.devflow.api.modules.media.dto.response.UploadedMediaResponse;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(name = "devflow.storage.minio.enabled", havingValue = "true")
public class MediaStorageService {

    private final MinioClient minioClient;
    private final MinioStorageProperties properties;
    private volatile boolean bucketReady;

    public MediaStorageService(MinioClient minioClient, MinioStorageProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    public UploadedMediaResponse uploadAvatar(Long userId, MultipartFile file) {
        return uploadImage(file, properties.getAvatarPrefix() + "/users/" + userId);
    }

    public UploadedMediaResponse uploadPostCover(Long userId, MultipartFile file) {
        return uploadImage(file, properties.getPostCoverPrefix() + "/users/" + userId);
    }

    private UploadedMediaResponse uploadImage(MultipartFile file, String prefix) {
        validateImage(file);
        ensureBucketReady();

        String originalFilename = file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()
                ? "image"
                : file.getOriginalFilename();
        String objectKey = buildObjectKey(prefix, originalFilename);

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectKey)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception exception) {
            throw new BusinessException(ResponseCode.MEDIA_UPLOAD_FAILED);
        }

        return new UploadedMediaResponse(
                buildPublicUrl(objectKey),
                objectKey,
                file.getContentType(),
                file.getSize()
        );
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResponseCode.MEDIA_INVALID_FILE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BusinessException(ResponseCode.MEDIA_INVALID_FILE);
        }

        if (file.getSize() > properties.getMaxFileSizeBytes()) {
            throw new BusinessException(ResponseCode.MEDIA_FILE_TOO_LARGE);
        }
    }

    private synchronized void ensureBucketReady() {
        if (bucketReady) {
            return;
        }

        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(properties.getBucket()).build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucket()).build());
            }

            String policy = """
                    {
                      "Version": "2012-10-17",
                      "Statement": [
                        {
                          "Effect": "Allow",
                          "Principal": {"AWS": ["*"]},
                          "Action": ["s3:GetObject"],
                          "Resource": ["arn:aws:s3:::%s/*"]
                        }
                      ]
                    }
                    """.formatted(properties.getBucket());
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(properties.getBucket())
                            .config(policy)
                            .build()
            );
            bucketReady = true;
        } catch (Exception exception) {
            throw new BusinessException(ResponseCode.MEDIA_UPLOAD_FAILED);
        }
    }

    private String buildObjectKey(String prefix, String originalFilename) {
        LocalDate today = LocalDate.now();
        String sanitized = sanitizeFilename(originalFilename);
        return "%s/%d/%02d/%s-%s".formatted(
                prefix,
                today.getYear(),
                today.getMonthValue(),
                UUID.randomUUID(),
                sanitized
        );
    }

    private String sanitizeFilename(String filename) {
        String sanitized = filename.replaceAll("[^A-Za-z0-9._-]", "-");
        return sanitized.isBlank() ? "image" : sanitized;
    }

    private String buildPublicUrl(String objectKey) {
        String baseUrl = properties.getPublicBaseUrl();
        if (baseUrl.endsWith("/")) {
            return baseUrl + objectKey;
        }
        return baseUrl + "/" + objectKey;
    }
}
