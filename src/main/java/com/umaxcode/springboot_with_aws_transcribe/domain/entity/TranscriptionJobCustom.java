package com.umaxcode.springboot_with_aws_transcribe.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import software.amazon.awssdk.services.transcribe.model.TranscriptionJobStatus;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transcription_job")
public class TranscriptionJobCustom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TranscriptionJobStatus status;
}
