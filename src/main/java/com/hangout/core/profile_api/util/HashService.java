package com.hangout.core.profile_api.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hangout.core.profile_api.exceptions.FileUploadFailedException;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HashService {
    private static final String ALGORITHM = "SHA3-512";

    /**
     * Computes the sha3-512 hash of a file and concatenates with original file
     * extesion to give the new file name
     * 
     * @param file
     * @return new file name for internal use
     */
    @WithSpan(value = "computing internal file name")
    public String computeInternalFilename(MultipartFile file) {
        byte[] data;
        try {
            data = file.getBytes();
            try {
                byte[] hash = MessageDigest.getInstance(ALGORITHM).digest(data);
                String checksum = new BigInteger(1, hash).toString(16);
                int lastDotIndex = file.getOriginalFilename().lastIndexOf('.');
                String originalfileExtension = file.getOriginalFilename().substring(lastDotIndex + 1);
                checksum += "." + originalfileExtension;
                return checksum;
            } catch (NoSuchAlgorithmException ex) {
                throw new IllegalArgumentException(ex);
            }
        } catch (IOException e) {
            throw new FileUploadFailedException(
                    "the file contents can not be processed may be the file is corrupted. Please check and reupload");
        }
    }
}
