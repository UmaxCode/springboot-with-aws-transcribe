package com.umaxcode.springboot_with_aws_transcribe.exception;


public class AudioTranscriptionException extends RuntimeException{

    public AudioTranscriptionException(String message) {
        super(message);
    }
}
