package com.umaxcode.springboot_with_aws_transcribe.service;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

     PutObjectResult upload(MultipartFile file) throws IOException;

     S3Object getObject(String objectKey);
}
