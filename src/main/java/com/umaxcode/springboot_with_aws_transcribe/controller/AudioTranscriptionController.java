package com.umaxcode.springboot_with_aws_transcribe.controller;

import com.umaxcode.springboot_with_aws_transcribe.service.AudioTranscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transcribe")
public class AudioTranscriptionController {

    private final AudioTranscriptionService audioTranscriptionService;

    @PostMapping("/audio")
    public void uploadAudioFile(@RequestPart("file") MultipartFile file) throws IOException {

        log.debug("Upload audio file for transcription : {}", file);
        audioTranscriptionService.uploadAudioForTranscription(file);
        log.debug("Successfully transcribe audio file : {}", file);
    }

}
