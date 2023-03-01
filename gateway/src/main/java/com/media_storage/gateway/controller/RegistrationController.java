package com.media_storage.gateway.controller;

import com.media_storage.auth_data.model.request.ConfirmationRequest;
import com.media_storage.auth_data.model.request.RegistrationRequest;
import com.media_storage.auth_data.model.response.AuthResponse;
import com.media_storage.auth_data.model.response.RegistrationResponse;
import com.media_storage.core_data.model.request.UpdateUserRequest;
import com.media_storage.core_data.model.response.UserDataResponse;
import com.media_storage.gateway.service.RegistrationService;
import com.media_storage.gateway.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.media_storage.shared_data.constant.SharedConstants.USER_ID_ATTRIBUTE;

@Tag(name = "Registration API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@SecurityRequirement(name = "media-storage")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;

    @PutMapping(path =  "/registrations")
    @Operation(description = "Step 1 - Registration")
    public RegistrationResponse register(@RequestBody @Valid RegistrationRequest request){
        return registrationService.register(request);
    }

    @PostMapping("/{userId}/confirmations")
    @Operation(description = "Step 2 - Email conformation")
    public AuthResponse confirmEmail(
            @PathVariable Long userId,
            @RequestBody @Valid ConfirmationRequest request
    ) {
        return registrationService.confirmEmail(userId, request);
    }

    @PutMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(description = "Step 3 - Update user data")
    public UserDataResponse updateUser(
            @Parameter(hidden = true) @RequestAttribute(USER_ID_ATTRIBUTE) Long userId,
            @RequestPart @Valid UpdateUserRequest request,
            @RequestPart(required = false) MultipartFile photo
    ) {
        return userService.updateUser(userId, request, photo);
    }
}
