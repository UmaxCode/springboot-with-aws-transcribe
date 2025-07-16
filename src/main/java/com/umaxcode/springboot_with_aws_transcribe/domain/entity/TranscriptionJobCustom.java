package com.umaxcode.springboot_with_aws_transcribe.domain.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import software.amazon.awssdk.services.transcribe.model.TranscriptionJobStatus;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transcription_job")
public class TranscriptionJobCustom {

    private String id;

    private String name;

    private TranscriptionJobStatus status;

    private String audioFileUrl;

    private String transcript;
}
