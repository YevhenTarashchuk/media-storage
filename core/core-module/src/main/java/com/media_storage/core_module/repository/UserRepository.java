package com.media_storage.core_module.repository;

import com.media_storage.core_module.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findAllByPhoneIgnoreCase(String phone);
}
