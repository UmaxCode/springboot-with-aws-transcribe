package com.umaxcode.springboot_with_aws_transcribe.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.umaxcode.springboot_with_aws_transcribe.configs.AWSProperties;
import com.umaxcode.springboot_with_aws_transcribe.domain.dto.S3PutObjectResultDTO;
import com.umaxcode.springboot_with_aws_transcribe.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;
    private final AWSProperties awsProperties;


    @Override
    public S3PutObjectResultDTO upload(MultipartFile file) throws IOException {

        // Generate a unique file name to avoid overwrites
        String fileName = generateUniqueFileName(file.getOriginalFilename());

        // Set up metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // Create the upload request
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                awsProperties.getS3InputBucketName(),
                fileName,
                file.getInputStream(),
                metadata
        );

        // Upload the file
        PutObjectResult putObjectResult = s3Client.putObject(putObjectRequest);

        return S3PutObjectResultDTO.builder()
                .objectKey(fileName)
                .result(putObjectResult)
                .build();
    }

    @Override
    public S3Object getObject(String objectKey) {

        GetObjectRequest getObjectRequest = new GetObjectRequest(awsProperties.getS3OutputBucketName(), objectKey);
        return s3Client.getObject(getObjectRequest);
    }

    /**
     * Generates a unique file name by prepending a UUID to the original file name
     */
    private String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID() + "-" + originalFilename;
    }

}
