package com.media_storage.gateway.service;


import com.media_storage.core_client.CoreClient;
import com.media_storage.core_data.model.request.UpdateUserRequest;
import com.media_storage.core_data.model.response.UserDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CoreClient coreClient;

    public UserDataResponse updateUser(Long userId, UpdateUserRequest request, MultipartFile photo) {
        return coreClient.updateUser(userId, request, photo);
    }

    public UserDataResponse getUser(Long userId) {
        return coreClient.getUser(userId);
    }
}
