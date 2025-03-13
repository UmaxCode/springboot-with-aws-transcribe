package com.umaxcode.springboot_with_aws_transcribe.configs;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "application.aws")
public class AWSProperties {
    private String region;
    private String accessKey;
    private String secretKey;
    private String s3InputBucketName;
    private String s3OutputBucketName;
}