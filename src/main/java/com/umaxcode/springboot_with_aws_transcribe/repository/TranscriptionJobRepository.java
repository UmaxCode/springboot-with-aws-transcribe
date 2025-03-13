package com.umaxcode.springboot_with_aws_transcribe.repository;

import com.amazonaws.services.transcribe.model.TranscriptionJobStatus;
import com.umaxcode.springboot_with_aws_transcribe.domain.entity.TranscriptionJobCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranscriptionJobRepository extends JpaRepository<TranscriptionJobCustom, Long> {

    List<TranscriptionJobCustom> findAllByStatus(TranscriptionJobStatus status);
}
