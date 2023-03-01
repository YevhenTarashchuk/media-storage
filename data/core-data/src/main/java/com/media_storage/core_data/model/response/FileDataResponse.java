package com.media_storage.core_data.model.response;

import com.media_storage.core_data.enumeration.FileType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDataResponse {
    private Long id;
    private String name;
    private String url;
    private Long userId;
    private FileType fileType;
}
