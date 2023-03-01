package com.media_storage.gateway.service;

import com.media_storage.core_client.CoreClient;
import com.media_storage.core_data.enumeration.FileType;
import com.media_storage.core_data.model.response.FileDataResponse;
import com.media_storage.media_client.MediaClient;
import com.media_storage.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.media_storage.shared_data.constant.ExceptionConstants.ACCESS_DENIED;
import static com.media_storage.shared_data.constant.ExceptionConstants.INVALID_MEDIA_URL;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final CoreClient coreClient;
    private final MediaClient mediaClient;

    @Value("${pattern.urlPattern}")
    private String urlPattern;

    @SneakyThrows
    public StreamingResponseBody getFile(String url, Long userId) {
        validateUrl(url, userId);
        return mediaClient.getFile(url).getInputStream()::transferTo;
    }

    public FileDataResponse uploadFile(MultipartFile file, Long userId) {
        return coreClient.uploadFile(userId, file);
    }

    public FileDataResponse getFileData(Long userId, Long fileDataId) {
        return coreClient.getFileData(userId, fileDataId);
    }

    public Page<FileDataResponse> getFilesData(String search, Set<FileType> fileTypes, Long userId, Pageable pageable) {
        return coreClient.getFilesData(userId, search, fileTypes, pageable);
    }

    private void validateUrl(String url, Long userId) {
        Matcher matcher = Pattern.compile(urlPattern).matcher(url);

        ValidationUtil.validateOrBadRequest(
                matcher.matches(),
                String.format(INVALID_MEDIA_URL, url)
        );

        ValidationUtil.validateOrFileAccessDenied(
                Objects.equals(Long.parseLong(matcher.group(1)), userId),
                ACCESS_DENIED
        );
    }
}
