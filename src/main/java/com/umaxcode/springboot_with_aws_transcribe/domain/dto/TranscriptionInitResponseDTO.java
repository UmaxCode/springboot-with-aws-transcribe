package com.umaxcode.springboot_with_aws_transcribe.domain.dto;

import lombok.Builder;

@Builder
public record TranscriptionInitResponseDTO(
        String message
) {
}
