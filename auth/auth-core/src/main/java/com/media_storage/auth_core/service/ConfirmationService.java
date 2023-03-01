package com.media_storage.auth_core.service;

import com.media_storage.auth_core.entity.ConfirmationEntity;
import com.media_storage.auth_core.repository.ConfirmationRepository;
import com.media_storage.shared.util.LocalDateTimeUtil;
import com.media_storage.shared.util.ValidationUtil;
import com.media_storage.shared_data.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.media_storage.auth_data.constant.AuthConstants.CODE_RESEND_TIME_IN_MIN;
import static com.media_storage.auth_data.constant.AuthConstants.CONFIRMATION_EXPIRATION_TIME_IN_MIN;
import static com.media_storage.shared_data.constant.ExceptionConstants.CODE_IS_EXPIRED;
import static com.media_storage.shared_data.constant.ExceptionConstants.CODE_RESEND_NOT_AVAILABLE;
import static com.media_storage.shared_data.constant.ExceptionConstants.CONFIRMATION_NOT_EXISTS;
import static com.media_storage.shared_data.constant.ExceptionConstants.INVALID_CODE;

@Service
@RequiredArgsConstructor
public class ConfirmationService {

    private final ConfirmationRepository confirmationRepository;
    private final BCryptPasswordEncoder encoder;


    public void addConfirmation(Long userId, String code) {
        ConfirmationEntity confirmation = confirmationRepository.findByUserId(userId)
                .orElse(new ConfirmationEntity())
                .setUserId(userId)
                .setCode(encoder.encode(code))
                .setExpirationTime(getExpirationTime());

        if (Objects.nonNull(confirmation.getCreatedAt())) {
            LocalDateTime codeResendTime = confirmation.getCreatedAt().plusMinutes(CODE_RESEND_TIME_IN_MIN);
            ValidationUtil.validateOrBadRequest(
                    LocalDateTimeUtil.getInstantNow().isAfter(codeResendTime),
                    String.format(CODE_RESEND_NOT_AVAILABLE, codeResendTime)
            );
        }

        confirmationRepository.save(confirmation);
    }

    public void validateCode(Long userId, String code) {
        ConfirmationEntity confirmation = confirmationRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(String.format(CONFIRMATION_NOT_EXISTS, userId)));

        ValidationUtil.validateOrBadRequest(
                LocalDateTimeUtil.getInstantNow().isBefore(confirmation.getExpirationTime()),
                CODE_IS_EXPIRED
        );

        ValidationUtil.validateOrBadRequest(
                encoder.matches(code, confirmation.getCode()),
                INVALID_CODE
        );

        confirmationRepository.deleteByUserId(userId);
    }

    private LocalDateTime getExpirationTime() {
        return LocalDateTimeUtil.getInstantNow().plusMinutes(CONFIRMATION_EXPIRATION_TIME_IN_MIN);
    }
}
