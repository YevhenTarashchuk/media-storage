package com.media_storage.shared_data.exception;

import org.springframework.http.HttpStatus;

public class FileNotFoundException extends FileException {
    public FileNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
