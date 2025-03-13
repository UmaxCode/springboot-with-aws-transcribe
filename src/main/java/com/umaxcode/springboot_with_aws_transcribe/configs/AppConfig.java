package com.umaxcode.springboot_with_aws_transcribe.configs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.AmazonTranscribeClientBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AppConfig {

    private final AWSProperties properties;

    @Bean
    public AmazonTranscribe transcribeClient() {
        log.debug("Initialize Transcribe Client");
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(properties.getAccessKey(), properties.getSecretKey());
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        return AmazonTranscribeClientBuilder.standard().withCredentials(awsStaticCredentialsProvider)
                .withRegion(properties.getRegion()).build();
    }

    @Bean
    public AmazonS3 s3Client() {
        log.debug("Initialize AWS S3 Client");
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(properties.getAccessKey(), properties.getSecretKey());
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        return AmazonS3ClientBuilder.standard().withCredentials(awsStaticCredentialsProvider).withRegion(properties.getRegion())
                .build();
    }
}
