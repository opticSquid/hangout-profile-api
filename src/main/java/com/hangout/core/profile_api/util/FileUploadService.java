package com.hangout.core.profile_api.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hangout.core.profile_api.exceptions.FileUploadFailed;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {
    private final MinioClient minioClient;
    @Value("${hangout.media.upload-bucket}")
    private String uploadBucket;

    /**
     * Uploads the given file with the given file name to Minio/s3 storage bucket
     * 
     * @param internalName  file name to be assigned to the file while storing
     * @param multipartFile file object to be stored
     * @return returns true if file upload is successful
     */
    public void uploadFile(String internalName, MultipartFile multipartFile) {
        try {
            ObjectWriteResponse writeResponse = minioClient
                    .putObject(PutObjectArgs
                            .builder()
                            .bucket(uploadBucket)
                            .object(internalName)
                            .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                            .contentType(multipartFile.getContentType()).build());
            log.info("File {} uploaded to bucket:{}, with eTag: {}", internalName, writeResponse.bucket(),
                    writeResponse.etag());
        } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
                | IllegalArgumentException
                | IOException ex) {
            log.error("File {} failed to upload, exception: {}, reason: {}", multipartFile.getOriginalFilename(),
                    ex,
                    ex.getCause());
            throw new FileUploadFailed(multipartFile.getOriginalFilename() + " failed to upload");
        }
    }
}
