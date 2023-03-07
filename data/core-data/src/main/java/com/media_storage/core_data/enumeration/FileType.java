package com.media_storage.core_data.enumeration;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum FileType {

    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio"),
    OTHER("other");

    private static final Tika tika = new Tika();
    private final String type;

    public static FileType determineType(String fileName) {
        String mimeType = tika.detect(fileName);

        return Arrays.stream(values())
                .filter(mediaType -> Objects.equals(mediaType.type, mimeType.split("/")[0]))
                .findFirst()
                .orElse(FileType.OTHER);
    }
}
