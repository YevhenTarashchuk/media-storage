package com.media_storage.auth_core.service;

import com.media_storage.auth_data.enumeration.Role;
import com.media_storage.auth_data.enumeration.UserStatus;
import com.media_storage.auth_data.model.request.ConfirmationRequest;
import com.media_storage.auth_data.model.request.RegistrationRequest;
import com.media_storage.auth_data.model.response.RegistrationResponse;
import com.media_storage.auth_data.model.response.UserDetailsResponse;
import com.media_storage.auth_core.entity.UserEntity;
import com.media_storage.auth_core.repository.UserRepository;
import com.media_storage.shared.util.CodeGeneratorUtil;
import com.media_storage.shared.util.ValidationUtil;
import com.media_storage.shared_data.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.media_storage.shared_data.constant.ExceptionConstants.EMAIL_EXISTS;
import static com.media_storage.shared_data.constant.ExceptionConstants.EMAIL_NOT_EXISTS;
import static com.media_storage.shared_data.constant.ExceptionConstants.INVALID_USER_STATUS;
import static com.media_storage.shared_data.constant.ExceptionConstants.USER_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CommunicationService communicationService;
    private final ConfirmationService confirmationService;
    private final BCryptPasswordEncoder encoder;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public RegistrationResponse registerUser(RegistrationRequest request) {
        UserEntity user = userRepository.findByEmailIgnoreCaseAndStatus(
                request.email(),
                UserStatus.EMAIL_CONFIRMATION
        ).orElse(new UserEntity());

        validateEmailExistence(request.email(), user.getId());

        modelMapper.map(request, user);

        user = userRepository.save(user
                .setPassword(encoder.encode(request.password()))
                .setStatus(UserStatus.EMAIL_CONFIRMATION)
                .setRole(Role.ROLE_USER));

        String code = CodeGeneratorUtil.generateCode();
        confirmationService.addConfirmation(user.getId(), code);

        communicationService.sendCodeEmail(user.getEmail(), code);

        return modelMapper.map(user, RegistrationResponse.class);
    }

    @Transactional
    public void confirmEmail(ConfirmationRequest request, Long userId) {
        UserEntity user = getUserById(userId);

        ValidationUtil.validateOrBadRequest(
                Objects.equals(user.getStatus(), UserStatus.EMAIL_CONFIRMATION),
                String.format(INVALID_USER_STATUS, user.getId(), UserStatus.EMAIL_CONFIRMATION)
        );

        confirmationService.validateCode(user.getId(), request.code());

        user.setStatus(UserStatus.ACTIVE);
    }

    public UserDetailsResponse getUserDetails(String email) {
        UserEntity user = userRepository.findByEmailIgnoreCaseAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(String.format(EMAIL_NOT_EXISTS, email)));

        return modelMapper.map(user, UserDetailsResponse.class);
    }

    public void validateUserExistence(Long userId) {
        ValidationUtil.validateOrNotFound(
                userRepository.existsById(userId),
                String.format(USER_NOT_EXISTS, userId)
        );
    }

    private void validateEmailExistence(String email, Long userId) {
        userRepository.findByEmailIgnoreCase(email).stream()
                .filter(user -> !Objects.equals(userId, user.getId()))
                .forEach(user ->
                        ValidationUtil.validateOrBadRequest(
                                !Objects.equals(email, user.getEmail()),
                                String.format(EMAIL_EXISTS, email)
                        ));
    }

    private UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_EXISTS, userId)));
    }
}
