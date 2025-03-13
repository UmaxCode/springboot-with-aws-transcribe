package com.umaxcode.springboot_with_aws_transcribe.service;

import com.amazonaws.services.transcribe.model.*;

public interface TranscriptionService {

    StartTranscriptionJobResult startTranscriptionJob(
            String inputS3BucketName,
            String s3ObjectKey,
            MediaFormat mediaFormat,
            LanguageCode languageCode,
            String outputS3BucketName
    );

    TranscriptionJob getTranscriptionJob(GetTranscriptionJobRequest request);

}
