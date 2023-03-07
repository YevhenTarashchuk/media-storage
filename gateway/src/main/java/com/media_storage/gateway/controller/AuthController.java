package com.media_storage.gateway.controller;

import com.media_storage.auth_data.model.request.AuthRequest;
import com.media_storage.auth_data.model.request.RefreshTokenRequest;
import com.media_storage.auth_data.model.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@SecurityRequirement(name = "media-storage")
public class AuthController {

    /**
     * The controller is used only to display the models in the swagger,
     * the implementation of the login and refresh token is in the security filters
     */

    @PostMapping
    @Operation(description = "User authentication")
    public AuthResponse auth(
            @RequestBody AuthRequest request
    ) {
        return null;
    }

    @PostMapping("/refresh-tokens")
    @Operation(description = "Refresh token")
    public AuthResponse refreshToken(
            @RequestBody RefreshTokenRequest refreshTokenRequest
    ) {
        return null;
    }
}
