package com.media_storage.communication_data.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailType {

    CODE_CONFIRMATION("code-template.html", "Verification email");

    private final String fileName;
    private final String subject;
}
