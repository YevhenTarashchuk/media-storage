package com.media_storage.shared.util;

import com.media_storage.shared_data.exception.BadRequestException;
import com.media_storage.shared_data.exception.FileAccessDeniedException;
import com.media_storage.shared_data.exception.FileNotFoundException;
import com.media_storage.shared_data.exception.NotFoundException;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;

@UtilityClass
public class ValidationUtil {

    @SneakyThrows
    public void validateOrBadRequest(boolean condition, String message) {
        validate(condition, message, BadRequestException.class);
    }

    @SneakyThrows
    public void validateOrNotFound(boolean condition, String message) {
        validate(condition, message, NotFoundException.class);
    }

    @SneakyThrows
    public void validateOrFileNotFound(boolean condition, String message) {
        validate(condition, message, FileNotFoundException.class);
    }

    @SneakyThrows
    public void validateOrFileAccessDenied(boolean condition, String message) {
        validate(condition, message, FileAccessDeniedException.class);
    }

    @SneakyThrows
    private void validate(boolean condition, String message, Class<? extends RuntimeException> exceptionClazz) {
        if (!condition) {
            Constructor<? extends RuntimeException> defaultConstructor = exceptionClazz.getConstructor(String.class);
            throw defaultConstructor.newInstance(message);
        }
    }
}
