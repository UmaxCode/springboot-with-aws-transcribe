package com.umaxcode.springboot_with_aws_transcribe.repository;

import com.umaxcode.springboot_with_aws_transcribe.domain.entity.TranscriptionJobCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import software.amazon.awssdk.services.transcribe.model.TranscriptionJobStatus;

import java.util.List;

public interface TranscriptionJobRepository extends JpaRepository<TranscriptionJobCustom, Long> {

    List<TranscriptionJobCustom> findAllByStatus(TranscriptionJobStatus status);
}
