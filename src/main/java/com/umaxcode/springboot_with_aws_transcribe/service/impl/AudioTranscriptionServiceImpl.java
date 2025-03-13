package com.umaxcode.springboot_with_aws_transcribe.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umaxcode.springboot_with_aws_transcribe.domain.dto.S3PutObjectResultDTO;
import com.umaxcode.springboot_with_aws_transcribe.domain.dto.TranscriptionResultDTO;
import com.umaxcode.springboot_with_aws_transcribe.domain.entity.TranscriptionJobCustom;
import com.umaxcode.springboot_with_aws_transcribe.repository.TranscriptionJobRepository;
import com.umaxcode.springboot_with_aws_transcribe.service.AudioTranscriptionService;
import com.umaxcode.springboot_with_aws_transcribe.service.S3Service;
import com.umaxcode.springboot_with_aws_transcribe.service.TranscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.transcribe.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static software.amazon.awssdk.services.transcribe.model.TranscriptionJobStatus.COMPLETED;
import static software.amazon.awssdk.services.transcribe.model.TranscriptionJobStatus.FAILED;

@Slf4j
@RequiredArgsConstructor
@Service
public class AudioTranscriptionServiceImpl implements AudioTranscriptionService {

    private final TranscriptionJobRepository jobRepository;
    private final TranscriptionService transcriptionService;
    private final S3Service s3Service;
    private final List<SseEmitter> emitters = new ArrayList<>();
    private final ObjectMapper objectMapper;


    @Override
    public void uploadAudioForTranscription(MultipartFile file) throws IOException {
        S3PutObjectResultDTO uploadResult = s3Service.upload(file);
        String objectKey = uploadResult.objectKey();
        StartTranscriptionJobResponse startTranscriptionJobResult = transcriptionService.startTranscriptionJob(
                objectKey,
                MediaFormat.M4_A,  // TODO: change to support all media format
                LanguageCode.EN_US
        );

        String transcriptionJobName = startTranscriptionJobResult.transcriptionJob().transcriptionJobName();
        TranscriptionJobStatus transcriptionJobStatus = startTranscriptionJobResult.transcriptionJob().transcriptionJobStatus();

        TranscriptionJobCustom customTranscriptionJobInstance = TranscriptionJobCustom.builder()
                .name(transcriptionJobName)
                .status(transcriptionJobStatus)
                .build();

        jobRepository.save(customTranscriptionJobInstance);

        log.info("Start transcription job result: {}", startTranscriptionJobResult);
    }

    @Scheduled(fixedRate = 60000)
    @Override
    public void pollPendingTranscriptionJobs() {

        List<TranscriptionJobCustom> pendingJobs = jobRepository.findAllByStatus(TranscriptionJobStatus.IN_PROGRESS);

        log.info("Pending transcription jobs: {}", pendingJobs.size());

        for (TranscriptionJobCustom job : pendingJobs) {
            GetTranscriptionJobRequest request = GetTranscriptionJobRequest.builder()
                    .transcriptionJobName(job.getName())
                    .build();

            try {
                TranscriptionJob transcriptionJob = transcriptionService.getTranscriptionJob(request);
                TranscriptionJobStatus currentTransJobStatus = transcriptionJob.transcriptionJobStatus();

                if (!job.getStatus().equals(currentTransJobStatus)) {

                    if (COMPLETED.equals(currentTransJobStatus)) {

                        String objectKey = transcriptionJob.transcriptionJobName() + ".json";
                        ResponseInputStream<GetObjectResponse> s3Object = s3Service.getObject(objectKey);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object));
                        TranscriptionResultDTO extractedTranscriptData = extractTranscriptData(reader);

                        log.info("Content length {}", s3Object.response().contentLength());

                        // notify user when transcription is complete
                        for (SseEmitter emitter : emitters) {
                            emitter.send(SseEmitter.event()
                                    .name("Transcript Ready")
                                    .data(extractedTranscriptData));
                        }

                        jobRepository.delete(job);

                        return;

                    } else if (FAILED.equals(currentTransJobStatus)) {
                        job.setStatus(TranscriptionJobStatus.fromValue("FAILED"));

                        // TODO: add retry mechanism or notify user of the failure
                    }

                    job.setStatus(currentTransJobStatus);
                    jobRepository.save(job);
                }
            } catch (Exception e) {
                // Handle error (e.g., job not found)
                System.err.println("Error polling job status: " + e.getMessage());
            }
        }

    }

    private TranscriptionResultDTO extractTranscriptData(BufferedReader s3InputStream) throws IOException {

        // Parse JSON from S3 response
        JsonNode rootNode = objectMapper.readTree(s3InputStream);

        // Navigate to results -> transcripts -> first transcript
        JsonNode transcriptNode = rootNode.path("results").path("transcripts").get(0);
        String transcript = transcriptNode.path("transcript").asText();

        // Navigate to results -> audio_segments -> first segment
        JsonNode audioSegmentNode = rootNode.path("results").path("audio_segments").get(0);
        String startTime = audioSegmentNode.path("start_time").asText();
        String endTime = audioSegmentNode.path("end_time").asText();

        // Print extracted values
        System.out.println("Transcript: " + transcript);
        System.out.println("Start Time: " + startTime);
        System.out.println("End Time: " + endTime);

        return TranscriptionResultDTO.builder()
                .transcript(transcript)
                .start_time(startTime)
                .end_time(endTime)
                .build();
    }

    @Override
    public SseEmitter getAudioTranscript() {

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(sseEmitter);
        try {
            sseEmitter.send(SseEmitter.event().name("INIT").build());
        } catch (IOException ex) {
            emitters.remove(sseEmitter);
        }

        sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));
        sseEmitter.onTimeout(() -> emitters.remove(sseEmitter));
        sseEmitter.onError(e -> emitters.remove(sseEmitter));

        return sseEmitter;
    }
}
