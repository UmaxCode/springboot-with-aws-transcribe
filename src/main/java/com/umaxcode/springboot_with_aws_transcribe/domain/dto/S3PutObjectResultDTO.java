package com.umaxcode.springboot_with_aws_transcribe.domain.dto;

import lombok.Builder;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Builder
public record S3PutObjectResultDTO(
        String objectKey,
        PutObjectResponse result
) {
}
