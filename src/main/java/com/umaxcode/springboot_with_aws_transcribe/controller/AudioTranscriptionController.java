package com.umaxcode.springboot_with_aws_transcribe.controller;

import com.umaxcode.springboot_with_aws_transcribe.domain.dto.TranscriptionInitResponseDTO;
import com.umaxcode.springboot_with_aws_transcribe.service.AudioTranscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transcribe")
public class AudioTranscriptionController {

    private final AudioTranscriptionService audioTranscriptionService;

    @PostMapping("/audio")
    @ResponseStatus(HttpStatus.CREATED)
    public TranscriptionInitResponseDTO uploadAudioFile(@RequestPart("file") MultipartFile file) throws IOException {

        log.debug("Upload audio file for transcription : {}", file);
        audioTranscriptionService.uploadAudioForTranscription(file);
        log.debug("Successfully transcribe audio file : {}", file);

        return TranscriptionInitResponseDTO.builder()
                .message("Successfully upload audio file for transcription ")
                .build();
    }

}
