package com.media_storage.gateway.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_storage.auth_data.model.request.AuthRequest;
import com.media_storage.gateway.security.exception.AuthMethodNotSupportedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.media_storage.auth_data.constant.ExceptionConstants.INVALID_CREDENTIALS;


@Slf4j
public class LoginRequestFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final ObjectMapper objectMapper;
    private final String emailPattern;

    public LoginRequestFilter(
            String defaultProcessUrl,
            AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler,
            ObjectMapper objectMapper,
            String emailPattern
    ) {
        super(defaultProcessUrl);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.objectMapper = objectMapper;
        this.emailPattern = emailPattern;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            throw new AuthMethodNotSupportedException("Authentication method not supported");
        }

        AuthRequest authRequest = objectMapper.readValue(request.getReader(), AuthRequest.class);
        validateAuthRequest(authRequest);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                authRequest.email(),
                authRequest.password()
        );

        return this.getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.debug("Authentication Sucessful");
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        log.error("Authentication Failed");
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    private void validateAuthRequest(AuthRequest authRequest) {
        Predicate<AuthRequest> invalidEmail = request ->
                StringUtils.isBlank(request.email());
        Predicate<AuthRequest> invalidPattern = request ->
                !Pattern.compile(emailPattern).matcher(request.email()).matches();
        Predicate<AuthRequest> invalidPassword = request ->
                StringUtils.isBlank(request.password());

        Predicate<AuthRequest> invalidRequest = invalidEmail.or(invalidPattern).or(invalidPassword);

        if (invalidRequest.test(authRequest)) {
            throw new AuthenticationServiceException(INVALID_CREDENTIALS);
        }
    }
}
