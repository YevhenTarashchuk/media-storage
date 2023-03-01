package com.media_storage.auth_core.repository;

import com.media_storage.auth_core.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    Optional<TokenEntity> findByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
