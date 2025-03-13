package com.umaxcode.springboot_with_aws_transcribe.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.model.*;
import com.umaxcode.springboot_with_aws_transcribe.configs.AWSProperties;
import com.umaxcode.springboot_with_aws_transcribe.service.TranscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TranscriptionServiceImpl implements TranscriptionService {

    private final AmazonTranscribe transcribeClient;
    private final AmazonS3 s3Client;
    private final AWSProperties properties;

    @Override
    public StartTranscriptionJobResult startTranscriptionJob(String s3ObjectKey,
                                                             MediaFormat mediaFormat, LanguageCode languageCode) {

        log.debug("Start Transcription Job By ObjectKey {}", s3ObjectKey);

        // Create the Media object pointing to the S3 file
        Media media = new Media().withMediaFileUri(s3Client.getUrl(properties.getS3InputBucketName(), s3ObjectKey).toExternalForm());

        // Generate a unique job name
        String jobName = "transcription-" + UUID.randomUUID();

        // Build the request
        StartTranscriptionJobRequest startTranscriptionJobRequest = new StartTranscriptionJobRequest()
                .withTranscriptionJobName(jobName)
                .withMedia(media)
                .withMediaFormat(mediaFormat)
                .withLanguageCode(languageCode)
                .withOutputBucketName(properties.getS3OutputBucketName());

        return transcribeClient
                .startTranscriptionJob(startTranscriptionJobRequest);
    }

    @Override
    public TranscriptionJob getTranscriptionJob(GetTranscriptionJobRequest request) {

        return transcribeClient.getTranscriptionJob(request).getTranscriptionJob();
    }
}
