package com.media_storage.core_module.service;

import com.media_storage.core_module.repository.UserRepository;
import com.media_storage.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.media_storage.shared_data.constant.ExceptionConstants.PHONE_EXISTS;
import static com.media_storage.shared_data.constant.ExceptionConstants.USER_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserRepository userRepository;

    public void validateUserExistence(Long userId) {
        validateUserExistence(null, userId);
    }

    public void validateUserExistence(String phone, Long userId) {
        if (Objects.isNull(phone)) {
            ValidationUtil.validateOrNotFound(
                    userRepository.existsById(userId),
                    String.format(USER_NOT_EXISTS, userId)
            );
        } else {
            userRepository.findAllByPhoneIgnoreCase(phone).stream()
                    .filter(user -> !Objects.equals(userId, user.getId()))
                    .forEach(user ->
                            ValidationUtil.validateOrBadRequest(
                                    !Objects.equals(phone, user.getPhone()),
                                    String.format(PHONE_EXISTS, phone)
                            ));
        }
    }
}
