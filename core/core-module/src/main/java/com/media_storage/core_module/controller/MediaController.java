package com.media_storage.core_module.controller;

import com.media_storage.core_data.enumeration.FileType;
import com.media_storage.core_data.model.response.FileDataResponse;
import com.media_storage.core_module.service.MediaService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users/{userId}/media")
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDataResponse uploadFile(
            @PathVariable Long userId,
            @RequestPart MultipartFile file
    ) {
        return mediaService.uploadFile(file, userId);
    }

    @GetMapping("/file-data")
    public Page<FileDataResponse> getFilesData(
            @PathVariable Long userId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Set<FileType> fileTypes,
            @Parameter(hidden = true) Pageable pageable
    ) {
        return mediaService.getFilesData(search, fileTypes, userId, pageable);
    }

    @GetMapping("/file-data/{fileDataId}")
    public FileDataResponse getFileData(
            @PathVariable Long userId,
            @PathVariable Long fileDataId
    ) {
        return mediaService.getFileData(userId, fileDataId);
    }
}
