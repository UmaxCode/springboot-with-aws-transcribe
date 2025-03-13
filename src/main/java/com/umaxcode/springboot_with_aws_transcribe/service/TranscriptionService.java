package com.umaxcode.springboot_with_aws_transcribe.service;

import software.amazon.awssdk.services.transcribe.model.*;

public interface TranscriptionService {

    StartTranscriptionJobResponse startTranscriptionJob(
            String s3ObjectKey,
            MediaFormat mediaFormat,
            LanguageCode languageCode
    );

    TranscriptionJob getTranscriptionJob(GetTranscriptionJobRequest request);

}
