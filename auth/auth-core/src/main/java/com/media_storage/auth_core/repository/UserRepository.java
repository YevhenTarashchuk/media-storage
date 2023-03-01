package com.media_storage.auth_core.repository;

import com.media_storage.auth_data.enumeration.UserStatus;
import com.media_storage.auth_core.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findByEmailIgnoreCaseAndStatus(String email, UserStatus status);
}
