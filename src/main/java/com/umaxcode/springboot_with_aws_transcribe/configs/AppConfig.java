package com.umaxcode.springboot_with_aws_transcribe.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.transcribe.TranscribeClient;

@EnableScheduling
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AppConfig {

    private final AWSProperties properties;

    @Bean
    public TranscribeClient transcribeClientClient() {
        log.debug("Initialize Transcribe Client");

        return TranscribeClient.builder()
                .credentialsProvider(() -> AwsBasicCredentials.create(properties.getAccessKey(),
                        properties.getSecretKey()))
                .region(Region.of(properties.getRegion()))
                .build();
    }


    @Bean
    public S3Client s3Client() {
        log.debug("Initialize S3 Client");

        return S3Client.builder()
                .credentialsProvider(() -> AwsBasicCredentials.create(properties.getAccessKey(),
                        properties.getSecretKey()))
                .region(Region.of(properties.getRegion()))
                .build();
    }

}
