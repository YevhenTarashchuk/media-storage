package com.media_storage.core_module.service;

import com.media_storage.core_data.model.request.UpdateUserRequest;
import com.media_storage.core_data.model.response.UserDataResponse;
import com.media_storage.core_module.entity.UserEntity;
import com.media_storage.core_module.repository.UserRepository;
import com.media_storage.shared_data.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.media_storage.shared_data.constant.ExceptionConstants.USER_NOT_EXISTS;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserValidationService userValidationService;
    private final UserRepository userRepository;
    private final MediaService mediaService;
    private final ModelMapper modelMapper;

    @Transactional
    public UserDataResponse updateUser(UpdateUserRequest request, MultipartFile photo, Long userId) {
        userValidationService.validateUserExistence(request.phone(), userId);

        UserEntity user = userRepository.findById(userId).orElse(new UserEntity())
                .setId(userId)
                .setFirstName(request.firstName())
                .setLastName(request.lastName())
                .setPhone(request.phone());

        String photoUrl = mediaService.uploadAvatar(photo, user.getId(), user.getPhotoUrl());
        user.setPhotoUrl(photoUrl);

        return modelMapper.map(userRepository.save(user), UserDataResponse.class);
    }

    public UserDataResponse getUser(Long userId) {
        return modelMapper.map(getUserById(userId), UserDataResponse.class);
    }

    private UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_EXISTS, userId)));
    }
}
