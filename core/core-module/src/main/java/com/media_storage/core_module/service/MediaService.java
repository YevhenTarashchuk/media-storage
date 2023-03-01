package com.media_storage.core_module.service;

import com.media_storage.core_data.enumeration.FileType;
import com.media_storage.core_data.model.response.FileDataResponse;
import com.media_storage.core_module.entity.FileDataEntity;
import com.media_storage.core_module.repository.FileDataRepository;
import com.media_storage.media_client.MediaClient;
import com.media_storage.shared_data.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.media_storage.shared_data.constant.ExceptionConstants.FILE_DATA_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final UserValidationService userValidationService;
    private final FileDataRepository fileDataRepository;
    private final MediaClient mediaClient;
    private final ModelMapper modelMapper;

    public String uploadAvatar(MultipartFile file, Long userId, String existingUrl) {
        if (Objects.nonNull(file)) {
            String photoUrl = String.format("user/%s/avatar", userId);
            mediaClient.uploadFile(file, photoUrl);
            return photoUrl;
        } else if (Objects.nonNull(existingUrl)) {
            mediaClient.deleteFile(existingUrl);
        }
        return null;
    }

    @Transactional
    public FileDataResponse uploadFile(MultipartFile file, Long userId) {
        userValidationService.validateUserExistence(userId);

        FileType fileType = FileType.determineType(file.getOriginalFilename());
        String fileUrl = String.format("user/%s/%s/%s", userId, fileType.name().toLowerCase(), UUID.randomUUID());

        FileDataEntity media = new FileDataEntity()
                .setName(file.getOriginalFilename())
                .setUserId(userId)
                .setFileType(fileType)
                .setUrl(fileUrl);
        media = fileDataRepository.save(media);

        mediaClient.uploadFile(file, fileUrl);

        return modelMapper.map(media, FileDataResponse.class);
    }

    public FileDataResponse getFileData(Long userId, Long fileDataId) {
        FileDataEntity media = fileDataRepository.findByIdAndUserId(fileDataId, userId)
                .orElseThrow(() -> new NotFoundException(String.format(FILE_DATA_NOT_FOUND, fileDataId, userId)));

        return modelMapper.map(media, FileDataResponse.class);
    }

    public Page<FileDataResponse> getFilesData(
            String search,
            Collection<FileType> fileTypes,
            Long userId,
            Pageable pageable
    ) {
        userValidationService.validateUserExistence(userId);

        Page<FileDataEntity> fileDataEntities = searchByFilter(search, fileTypes, userId, pageable);

        List<FileDataResponse> fileDataResponses = fileDataEntities.stream().
                map(fileData -> modelMapper.map(fileData, FileDataResponse.class))
                .toList();

        return new PageImpl<>(fileDataResponses, pageable, fileDataEntities.getTotalElements());
    }

    private Page<FileDataEntity> searchByFilter(
            String search,
            Collection<FileType> fileTypes,
            Long userId,
            Pageable pageable
    ) {
        search = Objects.isNull(search) ? "" : search;

        if (Objects.isNull(fileTypes) || fileTypes.isEmpty()) {
            return fileDataRepository.findAllByUserIdAndNameContainingIgnoreCase(userId, search, pageable);
        }

        return fileDataRepository.findAllByUserIdAndNameContainingIgnoreCaseAndFileTypeIn(
                userId,
                search,
                fileTypes,
                pageable
        );
    }
}
