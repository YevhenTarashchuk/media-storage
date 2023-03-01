package com.media_storage.core_module.repository;

import com.media_storage.core_data.enumeration.FileType;
import com.media_storage.core_module.entity.FileDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface FileDataRepository extends JpaRepository<FileDataEntity, Long> {

    Optional<FileDataEntity> findByIdAndUserId(Long mediaId, Long userId);

    Page<FileDataEntity> findAllByUserIdAndNameContainingIgnoreCaseAndFileTypeIn(
            Long userId,
            String search,
            Collection<FileType> fileTypes,
            Pageable pageable
    );

    Page<FileDataEntity> findAllByUserIdAndNameContainingIgnoreCase(Long userId, String search, Pageable pageable);
}
