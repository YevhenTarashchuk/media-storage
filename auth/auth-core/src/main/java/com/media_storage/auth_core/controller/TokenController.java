package com.media_storage.auth_core.controller;

import com.media_storage.auth_data.model.AuthTokenModel;
import com.media_storage.auth_core.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class TokenController {

    private final TokenService tokenService;

    @PutMapping("/{userId}/tokens")
    public ResponseEntity<Void> addToken(
            @PathVariable Long userId,
            @RequestBody AuthTokenModel request
    ) {
        tokenService.addToken(request, userId);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/{userId}/tokens")
    public AuthTokenModel getToken(@PathVariable Long userId) {
        return tokenService.getToken(userId);
    }
}
