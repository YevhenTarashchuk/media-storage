package com.media_storage.auth_core.controller;

import com.media_storage.auth_data.model.request.ConfirmationRequest;
import com.media_storage.auth_data.model.request.RegistrationRequest;
import com.media_storage.auth_data.model.response.RegistrationResponse;
import com.media_storage.auth_data.model.response.UserDetailsResponse;
import com.media_storage.auth_core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @PutMapping("/registrations")
    public RegistrationResponse registerUser(@RequestBody RegistrationRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/{userId}/confirmations")
    public ResponseEntity<Void> confirmEmail(
            @PathVariable Long userId,
            @RequestBody ConfirmationRequest request
    ) {
        userService.confirmEmail(request, userId);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping
    public UserDetailsResponse getUserDetails(@RequestParam String email) {
        return userService.getUserDetails(email);
    }
}
