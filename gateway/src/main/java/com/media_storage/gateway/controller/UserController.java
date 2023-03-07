package com.media_storage.gateway.controller;

import com.media_storage.core_data.model.response.UserDataResponse;
import com.media_storage.gateway.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.media_storage.shared_data.constant.SharedConstants.USER_ID_ATTRIBUTE;


@Tag(name = "User API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@SecurityRequirement(name = "media-storage")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(description = "Get user data")
    public UserDataResponse getUser(@Parameter(hidden = true) @RequestAttribute(USER_ID_ATTRIBUTE) Long userId) {
        return userService.getUser(userId);
    }
}
