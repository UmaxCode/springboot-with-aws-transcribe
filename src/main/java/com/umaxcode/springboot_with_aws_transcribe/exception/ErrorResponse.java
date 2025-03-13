package com.umaxcode.springboot_with_aws_transcribe.exception;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String path;
    private String message;
    private String timestamp;
}
