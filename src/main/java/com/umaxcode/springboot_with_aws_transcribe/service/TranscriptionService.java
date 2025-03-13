package com.umaxcode.springboot_with_aws_transcribe.service;

import com.amazonaws.services.transcribe.model.*;

public interface TranscriptionService {

    StartTranscriptionJobResult startTranscriptionJob(
            String s3ObjectKey,
            MediaFormat mediaFormat,
            LanguageCode languageCode
    );

    TranscriptionJob getTranscriptionJob(GetTranscriptionJobRequest request);

}
