package com.umaxcode.springboot_with_aws_transcribe.domain.dto;

import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.Builder;

@Builder
public record S3PutObjectResultDTO(
        String objectKey,
        PutObjectResult result
) {
}
