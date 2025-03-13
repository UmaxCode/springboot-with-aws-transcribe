package com.umaxcode.springboot_with_aws_transcribe.service;

import com.amazonaws.services.s3.model.S3Object;
import com.umaxcode.springboot_with_aws_transcribe.domain.dto.S3PutObjectResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    S3PutObjectResultDTO upload(MultipartFile file) throws IOException;

    S3Object getObject(String objectKey);
}
