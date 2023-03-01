package com.media_storage.shared_data.exception;

import org.springframework.http.HttpStatus;

public class FileAccessDeniedException extends FileException {

    public FileAccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public FileAccessDeniedException() {
        super("Access denied", HttpStatus.FORBIDDEN);
    }
}
