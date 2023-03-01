package com.media_storage.auth_core.repository;

import com.media_storage.auth_core.entity.ConfirmationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, Long> {

    Optional<ConfirmationEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
