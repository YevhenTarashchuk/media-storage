package com.media_storage.auth_client;

import com.media_storage.auth_data.model.AuthTokenModel;
import com.media_storage.auth_data.model.request.ConfirmationRequest;
import com.media_storage.auth_data.model.request.RegistrationRequest;
import com.media_storage.auth_data.model.response.RegistrationResponse;
import com.media_storage.auth_data.model.response.UserDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("${auth.name}")
public interface AuthClient {

    @PutMapping(path = "${auth.registrationUrl}")
    RegistrationResponse register(@RequestBody RegistrationRequest request);

    @PostMapping(path = "${auth.confirmationUrl}")
    void confirmEmail(@PathVariable Long userId, @RequestBody ConfirmationRequest request);

    @GetMapping(path = "${auth.userUrl}")
    UserDetailsResponse getUserDetails(@RequestParam String email);

    @PutMapping(path = "${auth.tokenUrl}")
    void addToken(@PathVariable Long userId, @RequestBody AuthTokenModel request);

    @GetMapping(path = "${auth.tokenUrl}")
    AuthTokenModel getToken(@PathVariable Long userId);
}
