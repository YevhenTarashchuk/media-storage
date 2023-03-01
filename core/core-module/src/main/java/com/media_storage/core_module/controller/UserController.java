package com.media_storage.core_module.controller;

import com.media_storage.core_data.model.request.UpdateUserRequest;
import com.media_storage.core_data.model.response.UserDataResponse;
import com.media_storage.core_module.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @PutMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDataResponse updateUser(
            @PathVariable Long userId,
            @RequestPart UpdateUserRequest request,
            @RequestPart(required = false) MultipartFile photo
    ) {
        return userService.updateUser(request, photo, userId);
    }

    @GetMapping("/{userId}")
    public UserDataResponse getUser(@PathVariable Long userId) {
         return userService.getUser(userId);
    }
}
