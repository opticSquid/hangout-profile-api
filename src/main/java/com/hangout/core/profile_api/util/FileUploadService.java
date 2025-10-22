package com.hangout.core.profile_api.util;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hangout.core.profile_api.exceptions.FileUploadFailedException;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Client s3Client;

    @Value("${hangout.media.upload-bucket}")
    private String uploadBucket;

    /**
     * Uploads a file to S3 using AWS SDK v2.
     *
     * @param internalName  Name of the object in S3.
     * @param multipartFile File content to upload.
     */
    @WithSpan(kind = SpanKind.CLIENT, value = "uploading file to S3")
    public void uploadFile(String internalName, MultipartFile multipartFile) {
        log.info("Starting upload of file {} to S3 bucket {}", internalName, uploadBucket);
        try (InputStream inputStream = multipartFile.getInputStream()) {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(uploadBucket)
                    .key(internalName)
                    .contentType(multipartFile.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(inputStream, multipartFile.getSize()));

            log.info("File '{}' uploaded successfully to bucket '{}' with ETag '{}'",
                    internalName, uploadBucket, response.eTag());

        } catch (S3Exception e) {
            log.error("S3Exception uploading file '{}': {}", internalName, e.awsErrorDetails().errorMessage(), e);
            throw new FileUploadFailedException("S3 upload failed for file: " + internalName);
        } catch (IOException e) {
            log.error("IO error uploading file '{}': {}", internalName, e.getMessage(), e);
            throw new FileUploadFailedException("IO error during upload: " + multipartFile.getOriginalFilename());
        } catch (Exception e) {
            log.error("Unexpected error uploading file '{}': {}", internalName, e.getMessage(), e);
            throw new FileUploadFailedException(multipartFile.getOriginalFilename() + " failed to upload");
        }
    }
}
