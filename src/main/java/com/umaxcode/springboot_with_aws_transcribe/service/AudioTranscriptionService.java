package com.umaxcode.springboot_with_aws_transcribe.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AudioTranscriptionService {

    void uploadAudioForTranscription(MultipartFile file) throws IOException;

    void pollPendingTranscriptionJobs();
}
