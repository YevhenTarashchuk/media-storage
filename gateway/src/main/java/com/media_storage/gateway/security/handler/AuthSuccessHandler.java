package com.media_storage.gateway.security.handler;

import com.media_storage.auth_client.AuthClient;
import com.media_storage.auth_data.model.AuthTokenModel;
import com.media_storage.auth_data.model.response.AuthResponse;
import com.media_storage.gateway.security.CustomUserDetails;
import com.media_storage.gateway.security.config.BCryptEncoder;
import com.media_storage.gateway.security.util.JwtUtilService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtilService jwtUtilService;
    private final ObjectMapper objectMapper;
    private final AuthClient authClient;
    private final BCryptEncoder encoder;


    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomUserDetails authenticationDetails = authentication instanceof UsernamePasswordAuthenticationToken
                ? (CustomUserDetails) authentication.getDetails()
                : (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtUtilService.generateToken(authenticationDetails).token();
        String refreshToken = jwtUtilService.generateRefreshToken(authenticationDetails).token();

        AuthTokenModel authTokenModel = new AuthTokenModel()
                .setAccessToken(encoder.encodeToken(accessToken))
                .setRefreshToken(encoder.encodeToken(refreshToken));

        authClient.addToken(authenticationDetails.getUserId(), authTokenModel);

        AuthResponse authResponse = new AuthResponse()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);

        log.debug("Generating tokens for authenticated user");

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), authResponse);

        clearAuthenticationAttributes(request);
    }

    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
