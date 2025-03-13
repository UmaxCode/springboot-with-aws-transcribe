package com.umaxcode.springboot_with_aws_transcribe.service;

import com.umaxcode.springboot_with_aws_transcribe.domain.dto.S3PutObjectResultDTO;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;

public interface S3Service {

    S3PutObjectResultDTO upload(MultipartFile file) throws IOException;

    ResponseInputStream<GetObjectResponse> getObject(String objectKey);
}
