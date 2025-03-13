package com.umaxcode.springboot_with_aws_transcribe.service.impl;

import com.umaxcode.springboot_with_aws_transcribe.configs.AWSProperties;
import com.umaxcode.springboot_with_aws_transcribe.service.TranscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TranscriptionServiceImpl implements TranscriptionService {

    private final TranscribeClient transcribeClient;
    private final AWSProperties properties;

    @Override
    public StartTranscriptionJobResponse startTranscriptionJob(String s3ObjectKey,
                                                               MediaFormat mediaFormat, LanguageCode languageCode) {

        log.debug("Start Transcription Job By ObjectKey {}", s3ObjectKey);

        // Create the Media object pointing to the S3 file
        String s3Uri = "s3://" + properties.getS3InputBucketName() + "/" + s3ObjectKey;
        Media media = Media.builder()
                .mediaFileUri(s3Uri)
                .build();

        // Generate a unique job name
        String jobName = "transcription-" + UUID.randomUUID();

        // Build the request
        StartTranscriptionJobRequest startTranscriptionJobRequest = StartTranscriptionJobRequest.builder()
                .transcriptionJobName(jobName)
                .media(media)
                .mediaFormat(mediaFormat)
                .languageCode(languageCode)
                .outputBucketName(properties.getS3OutputBucketName())
                .build();

        return transcribeClient
                .startTranscriptionJob(startTranscriptionJobRequest);
    }

    @Override
    public TranscriptionJob getTranscriptionJob(GetTranscriptionJobRequest request) {

        return transcribeClient.getTranscriptionJob(request).transcriptionJob();
    }
}
