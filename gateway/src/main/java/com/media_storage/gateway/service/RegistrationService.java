package com.media_storage.gateway.service;

import com.media_storage.auth_client.AuthClient;
import com.media_storage.auth_data.enumeration.Role;
import com.media_storage.auth_data.model.AuthTokenModel;
import com.media_storage.auth_data.model.request.ConfirmationRequest;
import com.media_storage.auth_data.model.request.RegistrationRequest;
import com.media_storage.auth_data.model.response.AuthResponse;
import com.media_storage.auth_data.model.response.RegistrationResponse;
import com.media_storage.gateway.security.CustomUserDetails;
import com.media_storage.gateway.security.config.BCryptEncoder;
import com.media_storage.gateway.security.util.JwtUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final JwtUtilService jwtUtilService;
    private final AuthClient authClient;
    private final BCryptEncoder encoder;

    public RegistrationResponse register(RegistrationRequest request) {
        return authClient.register(request);
    }

    public AuthResponse confirmEmail(Long userId, ConfirmationRequest request) {
        authClient.confirmEmail(userId, request);

        CustomUserDetails userDetails = new CustomUserDetails(userId, Role.ROLE_USER);

        String accessToken = jwtUtilService.generateToken(userDetails).token();
        String refreshToken = jwtUtilService.generateRefreshToken(userDetails).token();

        AuthTokenModel authTokenModel = new AuthTokenModel()
                .setAccessToken(encoder.encodeToken(accessToken))
                .setRefreshToken(encoder.encodeToken(refreshToken));

        authClient.addToken(userId, authTokenModel);

        return new AuthResponse()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);
    }
}
