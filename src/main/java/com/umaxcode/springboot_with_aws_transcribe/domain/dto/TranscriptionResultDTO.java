package com.umaxcode.springboot_with_aws_transcribe.domain.dto;

import lombok.Builder;

@Builder
public record TranscriptionResultDTO(
        String text,
        String timeStamps,
        String confidenceScore
) {
}
