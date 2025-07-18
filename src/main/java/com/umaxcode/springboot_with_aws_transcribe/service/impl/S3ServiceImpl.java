package com.umaxcode.springboot_with_aws_transcribe.service.impl;

import com.umaxcode.springboot_with_aws_transcribe.configs.AWSProperties;
import com.umaxcode.springboot_with_aws_transcribe.domain.dto.S3PutObjectResultDTO;
import com.umaxcode.springboot_with_aws_transcribe.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final AWSProperties awsProperties;


    @Override
    public S3PutObjectResultDTO upload(MultipartFile file) throws IOException {

        // Generate a unique file name to avoid overwrites
        String fileName = generateUniqueFileName(file.getOriginalFilename());

        // Set up metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("content-type", file.getContentType());
        metadata.put("file-size", String.valueOf(file.getSize()));

        // Create the upload request
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsProperties.getS3InputBucketName())
                .key(fileName)
                .metadata(metadata)
                .build();

        // Upload the file
        PutObjectResponse putObjectResult = s3Client.putObject(putObjectRequest,
                RequestBody.fromBytes(file.getBytes()));

        return S3PutObjectResultDTO.builder()
                .objectKey(fileName)
                .result(putObjectResult)
                .build();
    }

    @Override
    public ResponseInputStream<GetObjectResponse> getObject(String objectKey) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsProperties.getS3OutputBucketName())
                .key(objectKey)
                .build();
        return s3Client.getObject(getObjectRequest);
    }

    /**
     * Generates a unique file name by prepending a UUID to the original file name
     */
    private String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID() + "-" + originalFilename;
    }

    @Override
    public URL generatePreSignedUrl(String objectKey, int expirationInSeconds) throws IOException {

        PresignedGetObjectRequest presignedGetObjectRequest = PresignedGetObjectRequest.builder()
                .expiration(Instant.ofEpochSecond(expirationInSeconds))
                .signedPayload(SdkBytes.fromUtf8String(objectKey))
                .build();

        return presignedGetObjectRequest.url();
    }
}
