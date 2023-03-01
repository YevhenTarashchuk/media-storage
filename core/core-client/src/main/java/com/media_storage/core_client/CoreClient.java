package com.media_storage.core_client;

import com.media_storage.core_data.enumeration.FileType;
import com.media_storage.core_data.model.request.UpdateUserRequest;
import com.media_storage.core_data.model.response.FileDataResponse;
import com.media_storage.core_data.model.response.UserDataResponse;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;


@FeignClient(value = "${core.name}")
public interface CoreClient {

    @PutMapping(path = "${core.userIdUrl}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    UserDataResponse updateUser(
            @PathVariable Long userId,
            @RequestPart UpdateUserRequest request,
            @RequestPart(required = false) MultipartFile photo
    );

    @GetMapping(path = "${core.userIdUrl}")
    UserDataResponse getUser(@PathVariable Long userId);

    @PostMapping(path = "${core.mediaUrl}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    FileDataResponse uploadFile(@PathVariable Long userId, @RequestPart MultipartFile file);

    @GetMapping(path = "${core.fileDataIdUrl}")
    FileDataResponse getFileData(@PathVariable Long userId, @PathVariable Long fileDataId);

    @GetMapping(path = "${core.fileDataUrl}")
    Page<FileDataResponse> getFilesData(
            @PathVariable Long userId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Set<FileType> fileTypes,
            @Parameter(hidden = true) Pageable pageable
    );
}
