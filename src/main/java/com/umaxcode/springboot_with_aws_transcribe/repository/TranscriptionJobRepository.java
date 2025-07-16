package com.umaxcode.springboot_with_aws_transcribe.repository;

import com.umaxcode.springboot_with_aws_transcribe.domain.entity.TranscriptionJobCustom;
import org.springframework.data.mongodb.repository.MongoRepository;
import software.amazon.awssdk.services.transcribe.model.TranscriptionJobStatus;

import java.util.List;

public interface TranscriptionJobRepository extends MongoRepository<TranscriptionJobCustom, String> {

    List<TranscriptionJobCustom> findAllByStatus(TranscriptionJobStatus status);
}
