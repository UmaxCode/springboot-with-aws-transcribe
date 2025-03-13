package com.umaxcode.springboot_with_aws_transcribe.service.impl;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.transcribe.model.*;
import com.umaxcode.springboot_with_aws_transcribe.domain.dto.S3PutObjectResultDTO;
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

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AudioTranscriptionServiceImpl implements AudioTranscriptionService {

    private final TranscriptionJobRepository jobRepository;
    private final TranscriptionService transcriptionService;
    private final S3Service s3Service;


    @Override
    public void uploadAudioForTranscription(MultipartFile file) throws IOException {
        S3PutObjectResultDTO uploadResult = s3Service.upload(file);
        String objectKey = uploadResult.objectKey();
        StartTranscriptionJobResult startTranscriptionJobResult = transcriptionService.startTranscriptionJob(
                objectKey,
                MediaFormat.Mp4,  // TODO: change to support all media format
                LanguageCode.EnUS
        );

        String transcriptionJobName = startTranscriptionJobResult.getTranscriptionJob().getTranscriptionJobName();
        String transcriptionJobStatus = startTranscriptionJobResult.getTranscriptionJob().getTranscriptionJobStatus();
        TranscriptionJobStatus transcriptionJobStatusEnum = TranscriptionJobStatus.valueOf(transcriptionJobStatus);

        TranscriptionJobCustom customTranscriptionJobInstance = TranscriptionJobCustom.builder()
                .name(transcriptionJobName)
                .status(transcriptionJobStatusEnum)
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
            GetTranscriptionJobRequest request = new GetTranscriptionJobRequest()
                    .withTranscriptionJobName(job.getName());

            try {
                TranscriptionJob transcriptionJob = transcriptionService.getTranscriptionJob(request);
                String currentTransJobStatus = transcriptionJob.getTranscriptionJobStatus();

                if (!job.getStatus().toString().equalsIgnoreCase(currentTransJobStatus)) {
                    job.setStatus(TranscriptionJobStatus.fromValue(currentTransJobStatus));

                    if ("COMPLETED".equalsIgnoreCase(currentTransJobStatus)) {

                        S3Object transcriptionResultObject = s3Service.getObject(request.getTranscriptionJobName());

                        System.out.println(transcriptionResultObject.getObjectContent());

                        jobRepository.delete(job);

                        return;

                    } else if ("FAILED".equalsIgnoreCase(currentTransJobStatus)) {
                        job.setStatus(TranscriptionJobStatus.fromValue("FAILED"));

                        // TODO: add retry mechanism or notify user of the failure
                    }

                    jobRepository.save(job);
                }
            } catch (Exception e) {
                // Handle error (e.g., job not found)
                System.err.println("Error polling job status: " + e.getMessage());
            }
        }

    }
}
