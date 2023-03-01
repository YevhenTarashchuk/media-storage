package com.media_storage.gateway.controller;

import com.media_storage.core_data.enumeration.FileType;
import com.media_storage.core_data.model.response.FileDataResponse;
import com.media_storage.gateway.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Set;

import static com.media_storage.shared_data.constant.SharedConstants.USER_ID_ATTRIBUTE;

@Tag(name = "Media API")
@RestController
@RequestMapping("/v1/media")
@RequiredArgsConstructor
@SecurityRequirement(name = "media-storage")
public class MediaController {

    private final MediaService mediaService;

    @GetMapping(produces = {
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    })
    @Operation(description = "Get file by url")
    public ResponseEntity<StreamingResponseBody> getFile(
            @Parameter(hidden = true) @RequestAttribute(USER_ID_ATTRIBUTE) Long userId,
            @RequestParam String url) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(mediaService.getFile(url, userId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Upload file")
    public FileDataResponse uploadFile(
            @Parameter(hidden = true) @RequestAttribute(USER_ID_ATTRIBUTE) Long userId,
            @RequestPart MultipartFile file
    ) {
        return mediaService.uploadFile(file, userId);
    }

    @GetMapping("/file-data")
    @Operation(description = "Get files data")
    @Parameter(
            in = ParameterIn.QUERY, name = "page",
            schema = @Schema(type = "integer", defaultValue = "0")
    )
    @Parameter(
            in = ParameterIn.QUERY, name = "size",
            schema = @Schema(type = "integer", defaultValue = "10")
    )
    @Parameter(in = ParameterIn.QUERY, name = "sort")
    public Page<FileDataResponse> getFilesData(
            @Parameter(hidden = true) @RequestAttribute(USER_ID_ATTRIBUTE) Long userId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Set<FileType> fileTypes,
            @Parameter(hidden = true) Pageable pageable
    ) {
        return mediaService.getFilesData(search, fileTypes, userId, pageable);
    }

    @GetMapping("/file-data/{fileDataId}")
    @Operation(description = "Get file data")
    public FileDataResponse getFileData(
            @Parameter(hidden = true) @RequestAttribute(USER_ID_ATTRIBUTE) Long userId,
            @PathVariable Long fileDataId
    ) {
        return mediaService.getFileData(userId, fileDataId);
    }
}
